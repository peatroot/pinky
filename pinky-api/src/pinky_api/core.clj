(ns pinky-api.core
  (:require
    [clara.rules :refer :all]
    [clojure.java.io :as io]
    [cheshire.core :as json])
  (:import java.util.zip.GZIPInputStream))

; loader utils
(defn load-lines-from-resource-file
  [filename] 
  (with-open
    [rdr (io/reader (java.util.zip.GZIPInputStream. (io/input-stream (io/resource filename))))]
    (into-array (line-seq rdr))))
(defn parse-json-as-keywords
  [line]
  (json/parse-string line true))
(defn load-json-lines 
  [filename]
  (doall (map parse-json-as-keywords (load-lines-from-resource-file filename))))

; load records into memory
(def genes (load-json-lines "records/gene.jsonl.gz"))
(def drugs (load-json-lines "records/drug.jsonl.gz"))
(def locus-to-genes (load-json-lines "records/locus-to-gene.jsonl.gz"))
(def mechanism-of-actions (load-json-lines "records/mechanism-of-action.jsonl.gz"))
(def protein-protein-interactions (load-json-lines "records/protein-protein-interactions.jsonl.gz"))

; TODO: move to external file (all records)
(defrecord Gene [ensg-id hgnc-id name symbol])
(defrecord Drug [chembl-id name])
(defrecord L2G [ensg-id study-id efo-id variant-id post-prob score])
(defrecord MechanismOfAction [ensg-id chembl-id type mechanism-of-action])
(defrecord ProteinProteinInteraction [ensg-id-1 ensg-id-2 score])

(defquery get-gene-by-symbol
  "Query to find a gene given the symbol."
  [:?symbol]
  [?gene <- Gene (= ?symbol symbol)])

(defquery get-drug-by-name
  "Query to find a drug given the name."
  [:?name]
  [?drug <- Drug (= ?name name)])

(defquery get-loci-for-gene-by-ensg-id
  "Query to find loci for a gene given the ensgId"
  [:?ensg-id]
  [?l2g <- L2G (= ?ensg-id ensg-id)])

(defquery get-drugs-for-gene-by-ensg-id
  "Query to find drugs (and MoA) for a gene given the ensgId"
  [:?ensg-id]
  [?moa <- MechanismOfAction (= ?ensg-id ensg-id)])

(defquery get-interactors-for-gene-by-ensg-id
  "Query to find interactors for a gene given the ensgId"
  [:?ensg-id]
  [?ppi <- ProteinProteinInteraction (= ?ensg-id ensg-id-1)])

(defn -main
  "Basic setup."
  [& args]
  (let [initial-session (-> (mk-session 'pinky-api.core)
      (insert-all (map (fn [g] (->Gene (g :ensgId) (g :hgncId) (g :name) (g :symbol))) genes))
      (insert-all (map (fn [d] (->Drug (d :chemblId) (d :name))) drugs))
      (insert-all (map (fn [d] (->L2G (d :ensgId) (d :studyId) (d :efoId) (d :variantId) (d :postProb) (d :score))) locus-to-genes))
      (insert-all (map (fn [d] (->MechanismOfAction (d :ensemblId) (d :chemblId) (d :mechanismOfActionType) (d :mechanismOfAction))) mechanism-of-actions))
      (insert-all (map (fn [d] (->ProteinProteinInteraction (d :gene1) (d :gene2) (d :score))) protein-protein-interactions))
      (fire-rules))]
    
    (println 
      (query initial-session get-gene-by-symbol :?symbol "BRAF"))
    (println 
      (query initial-session get-drug-by-name :?name "BEPRIDIL"))
    (println
      (query initial-session get-interactors-for-gene-by-ensg-id :?ensg-id "ENSG00000004059"))))
