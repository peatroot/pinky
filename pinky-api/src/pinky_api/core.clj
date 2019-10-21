(ns pinky-api.core
  (:require
   [clara.rules.accumulators :as acc]
   [clara.rules :refer :all]
   [pinky-api.loader :refer :all]))

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

; ; rules for inference
; (defrecord TherapeuticHypothesisRemoteDrugInteraction [drug mechanism-of-action-type drug-interacting-gene gwas-locus-gene locus study trait])
; (defrule infer-therapeutic-hypothesis--remote-drug-interaction
;   "Matches cases where:
;   * a drug Dr interacts with a gene G1 (via mechanism type T)
;   * gene G1 interacts with another gene G2 (via a protein protein interaction)
;   * gene G2 is associated with a GWAS study S (for trait E at locus L)"
;   [MechanismOfAction (= ?g1 ensg-id) (= ?dr chembl-id) (= ?t type)]
;   [ProteinProteinInteraction (= ?p1 ensg-id-1) (= ?p2 ensg-id-2)]
;   [L2G (= ?g2 ensg-id) (= ?l variant-id) (= ?s study-id) (= ?e efo-id)]
;   [:test (= ?g1 ?p1) (= ?g2 ?p2)]
;   =>
;   (insert! (->TherapeuticHypothesisRemoteDrugInteraction ?dr ?t ?g1 ?g2 ?l ?s ?e))
;   (println (format "TherapeuticHypothesisRemoteDrugInteraction(%s, %s, %s, %s, %s, %s, %s)" ?dr ?t ?g1 ?g2 ?l ?s ?e)))

(defrecord Interactors [ensg-id interactors])
(defrule get-interactors
  [?ensg-id <- Gene (= ?ensg-id ensg-id)]
  [?interactors <- (acc/all :ensg-id-2) :from [ProteinProteinInteraction (= ?ensg-id ensg-id-1)]]
  =>
  (insert! (->Interactors ?ensg-id (set ?interactors))))

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

; (defquery get-interactors-for-gene-by-ensg-id
;   "Query to find interactors for a gene given the ensgId"
;   [:?ensg-id]
;   [?ppi <- ProteinProteinInteraction (= ?ensg-id ensg-id-1)])

(defquery get-interactors-for-gene-by-ensg-id
  "Query to find interactors for a gene given the ensgId"
  [:?ensg-id]
  [?result <- Interactors (= ?ensg-id ensg-id)])

; ; works
; (defquery get-interactors-for-gene-by-ensg-id
;   "Query to find interactors for a gene given the ensgId"
;   [:?ensg-id]
;   [?interactors <- (acc/all :ensg-id-2) :from [ProteinProteinInteraction (= ?ensg-id ensg-id-1)]])

; (defrule get-interactors
;   [?ensg-id <- Gene (= ?ensg-id ensg-id)]
;   ; [?interactors <- (acc/all :interactor) :from [ProteinProteinInteraction (= ?ensg-id ensg-id-1) (= ?interactor ensg-id-2)]]
;   [?interactors <- (acc/all :ensg-id-2) :from [ProteinProteinInteraction (= ?ensg-id ensg-id-1)]]

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

    ; (println 
    ;   (query initial-session get-gene-by-symbol :?symbol "BRAF"))
    ; (println 
    ;   (query initial-session get-drug-by-name :?name "BEPRIDIL"))

    (println
     (query initial-session get-interactors-for-gene-by-ensg-id :?ensg-id "ENSG00000157764")) ; BRAF

    ; (println
    ;   (get (query initial-session get-interactors-for-gene-by-ensg-id :?ensg-id "ENSG00000157764")) :interactors) ; BRAF
    ; (println
    ;   (get (query initial-session get-interactors-for-gene-by-ensg-id :?ensg-id "ENSG00000171862")) :interactors) ; PTEN
    ))
