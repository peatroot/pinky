(ns pinky-api.engine
  (:require
   [clara.rules.accumulators :as acc]
   [clara.rules :refer :all]))

; -----------------------------------------------------------
; base records/facts
; -----------------------------------------------------------

; the following record types map directly to the input files
(defrecord Gene [ensg-id hgnc-id name symbol])
(defrecord Drug [chembl-id name])
(defrecord L2G [ensg-id study-id efo-id variant-id post-prob score])
(defrecord MechanismOfAction [ensg-id chembl-id type mechanism-of-action])
(defrecord ProteinProteinInteraction [ensg-id-1 ensg-id-2 score])

; -----------------------------------------------------------
; inferred records/facts
; -----------------------------------------------------------

; the following record/rule infers the interactor set per gene
(defrecord Interactors [ensg-id interactors])
(defrule get-interactors
  [Gene (= ?ensg-id ensg-id)]
  [?interactors <- (acc/all :ensg-id-2) :from [ProteinProteinInteraction (= ?ensg-id ensg-id-1)]]
  =>
  (insert! (->Interactors ?ensg-id (set ?interactors))))

; the following record/rule infers the genes associated to a disease (via any GWAS study and locus)
(defrecord DirectGWASGenes [efo-id ensg-ids])
(defrule get-direct-gwas-genes
  [?ensg-ids <- (acc/all :ensg-id) :from [L2G (= ?efo-id efo-id)]]
  =>
  (insert! (->DirectGWASGenes ?efo-id (set ?ensg-ids))))

; -----------------------------------------------------------
; queries
; -----------------------------------------------------------

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
  [?result <- Interactors (= ?ensg-id ensg-id)])

(defquery get-direct-gwas-genes-for-disease-by-efo-id
  "Query to find direct GWAS genes for a disease given the efoId"
  [:?efo-id]
  [?result <- DirectGWASGenes (= ?efo-id efo-id)])
