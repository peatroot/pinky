(ns pinky-api.core
  (:require
    [clara.rules :refer :all]
    [clojure.java.io :as io]
    [cheshire.core :as json]))

; TODO: move to external file (all records)
(defrecord Gene [ensg-id symbol])
(defrecord Trait [efo-id name])
(defrecord GeneGeneInteraction [ensg-id-1 ensg-id-2])
(defrecord IsSubTrait [efo-id-1 efo-id-2])
(defrecord TraitGeneAssociation [efo-id ensg-id])

; TODO: move to external file (all rules)
(defrule propagate-subtrait-association
    "An association with gene G of a subtrait S of trait T infers an association of gene G with trait T"
    [IsSubTrait (= ?subtrait efo-id-1) (= ?supertrait efo-id-2)]
    [TraitGeneAssociation (= ?trait efo-id) (= ?gene ensg-id)]
    [:test (= ?subtrait ?trait)]
    =>
    (insert! (->TraitGeneAssociation ?supertrait ?gene))
    (println (str ?supertrait " inferred to be associated with " ?gene)))

(defn -main
  "I don't do a whole lot."
  [& args]
  (-> (mk-session 'pinky-api.core)
    (insert (->Gene "ENSG00000121879" "PIK3CA")
            (->Gene "ENSG00000160789" "LMNA")
            (->Gene "ENSG00000049167" "ERCC8")
            (->Trait "EFO_0000701" "skin disease")
            (->Trait "Orphanet_68346" "Rare genetic skin disease")
            (->Trait "Orphanet_79389" "Premature aging")
            (->Trait "Orphanet_183484" "Genetic subcutaneous tissue disorder")
            (->IsSubTrait "Orphanet_183484" "Orphanet_68346")
            (->IsSubTrait "Orphanet_79389" "Orphanet_68346")
            (->IsSubTrait "Orphanet_68346" "EFO_0000701")
            (->TraitGeneAssociation "Orphanet_183484" "ENSG00000121879")
            (->TraitGeneAssociation "Orphanet_183484" "ENSG00000160789")
            (->TraitGeneAssociation "Orphanet_79389" "ENSG00000160789")
            (->TraitGeneAssociation "Orphanet_79389" "ENSG00000049167"))
    (fire-rules)))
