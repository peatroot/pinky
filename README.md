# Pinky

Experimental.

**Goal:** Interactive derivation of biological facts using an inference engine.

## Pipeline

See `pinky-pipeline` for details.

Initial data includes the following:

- Open Targets Platform evidence from ChEMBL (gene-drug-clinicalTrial-disease)
- Open Targets Genetics locus to gene scores (gene-locus-study-trait)
- StringDB (protein-protein)
- Ensembl (gene annotation)
- HGNC (gene annotation)
- UniProt (protein annotation)

## API

See `pinky-api` for details.

Inference managed by [clara-rules](http://www.clara-rules.org/), an implementation of the [Rete algorithm](https://en.wikipedia.org/wiki/Rete_algorithm). Custom inference rules are Clojure functions that match on records/facts derived from the pipeline data.

## UI

TODO!
