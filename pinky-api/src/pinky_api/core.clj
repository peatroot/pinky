(ns pinky-api.core
  (:require
   [clara.rules.accumulators :as acc]
   [clara.rules :refer :all]
   [pinky-api.loader :refer :all]
   [pinky-api.engine :refer :all]))

(defn load-session
  []
  (let [genes (load-json-lines "records/gene.jsonl.gz")
        drugs (load-json-lines "records/drug.jsonl.gz")
        locus-to-genes (load-json-lines "records/locus-to-gene.jsonl.gz")
        mechanism-of-actions (load-json-lines "records/mechanism-of-action.jsonl.gz")
        protein-protein-interactions (load-json-lines "records/protein-protein-interactions.jsonl.gz")
        s1 (-> (mk-session 'pinky-api.core 'pinky-api.engine)
               (insert-all (map (fn [g] (->Gene (g :ensgId) (g :hgncId) (g :name) (g :symbol))) genes))
               (insert-all (map (fn [d] (->Drug (d :chemblId) (d :name))) drugs))
               (insert-all (map (fn [d] (->L2G (d :ensgId) (d :studyId) (d :efoId) (d :variantId) (d :postProb) (d :score))) locus-to-genes))
               (insert-all (map (fn [d] (->MechanismOfAction (d :ensemblId) (d :chemblId) (d :mechanismOfActionType) (d :mechanismOfAction))) mechanism-of-actions))
               (insert-all (map (fn [d] (->ProteinProteinInteraction (d :gene1) (d :gene2) (d :score))) protein-protein-interactions))
               (fire-rules))]
    s1))

; (defn -main
;   "Basic setup."
;   [& args]
;   (let [initial-session (-> (mk-session 'pinky-api.core)
;                             (insert-all (map (fn [g] (->Gene (g :ensgId) (g :hgncId) (g :name) (g :symbol))) genes))
;                             (insert-all (map (fn [d] (->Drug (d :chemblId) (d :name))) drugs))
;                             (insert-all (map (fn [d] (->L2G (d :ensgId) (d :studyId) (d :efoId) (d :variantId) (d :postProb) (d :score))) locus-to-genes))
;                             (insert-all (map (fn [d] (->MechanismOfAction (d :ensemblId) (d :chemblId) (d :mechanismOfActionType) (d :mechanismOfAction))) mechanism-of-actions))
;                             (insert-all (map (fn [d] (->ProteinProteinInteraction (d :gene1) (d :gene2) (d :score))) protein-protein-interactions))
;                             (fire-rules))]
