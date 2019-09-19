#!make

# load environment variables
include .env
export

EVIDENCE_FILES := $(filter OT_EVIDENCE_%, $(.VARIABLES))

# go for it!
pipeline: | download_data_raw

# download raw data
download_data_raw: $(EVIDENCE_FILES) | make_data_directory

$(EVIDENCE_FILES):
	gsutil -m cp -n $(OT_BUCKET_RELEASES)/$($@) data/raw/$($@)

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