#!make

# load environment variables
include .env
export

FILES_EVIDENCE := $(filter OT_EVIDENCE_%, $(.VARIABLES))
FILES_ANNOTATION_TARGET := $(filter OT_TARGET_ANNOTATION_%, $(.VARIABLES))

FILE_ENTITY_GENE := gene.jsonl.gz
FILE_ENTITY_PROTEIN := protein.jsonl.gz
# FILE_ENTITY_CLINICAL_TRIAL := clinical-trial.jsonl.gz

# go for it!
pipeline: create_entities

create_entities: data/processed/$(FILE_ENTITY_GENE) data/processed/$(FILE_ENTITY_PROTEIN)

# build gene entity
data/processed/$(FILE_ENTITY_GENE): data/raw/$(OT_TARGET_ANNOTATION_HGNC) | download_data_raw
	jq -c '.response.docs[] | select(.ensembl_gene_id) | {hgncId: .hgnc_id, ensgId: .ensembl_gene_id, symbol: .symbol, name: .name}' $< | gzip > $@

# build protein entity
data/processed/$(FILE_ENTITY_PROTEIN): data/raw/uniprot.jsonl.gz | download_data_raw
	gzcat $< | jq -c '.uniprot.entry[] | {uniprotId: .accession, sequence: .sequence."#text", sequenceLength: .sequence."@length", mass: .sequence."@mass"}' | gzip > $@
data/raw/uniprot.jsonl.gz: data/raw/$(OT_TARGET_ANNOTATION_UNIPROT) | download_data_raw
	gzcat $< | xq '.' | gzip > $@

# # build clinical-trial entity
# data/processed/$(FILE_ENTITY_CLINICAL_TRIAL): data/raw/$(OT_EVIDENCE_CHEMBL) | download_data_raw
# 	jq -c '.response.docs[] | qselect(.ensembl_gene_id) | {hgncId: .hgnc_id, ensgId: .ensembl_gene_id, symbol: .symbol, name: .name}' $< | gzip > $@

# download raw data
download_data_raw: $(FILES_ANNOTATION_TARGET) $(FILES_EVIDENCE) | make_data_directory

$(FILES_EVIDENCE):
	gsutil -m cp -n $(OT_BUCKET_EVIDENCE)/$($@) data/raw/$($@)

$(FILES_ANNOTATION_TARGET):
	gsutil -m cp -n $(OT_BUCKET_ANNOTATION)/$($@) data/raw/$($@)

# ensure directory structure exists
make_data_directory:
	mkdir -p data
	mkdir -p data/raw
	mkdir -p data/processed

# remove data files
clean_data: clean_data_processed clean_data_raw

# remove processed data files
clean_data_processed:
	rm -rf data/processed

# remove downloaded data files
clean_data_raw:
	rm -rf data/raw