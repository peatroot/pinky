#!make

# load environment variables
include .env
export

# go for it!
pipeline: download_data_raw

# download raw data
# (TODO: use pattern matching here)
download_data_raw: make_data_directory
	gsutil -m cp $(OT_BUCKET_RELEASES)/$(OT_EVIDENCE_CHEMBL) data/raw/$(OT_EVIDENCE_CHEMBL)
	gsutil -m cp $(OT_BUCKET_RELEASES)/$(OT_EVIDENCE_GWAS_CATALOG) data/raw/$(OT_EVIDENCE_GWAS_CATALOG)

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