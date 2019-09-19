#!make

# load environment variables
include .env
export

FILES_EVIDENCE := $(filter OT_EVIDENCE_%, $(.VARIABLES))
FILES_ANNOTATION_TARGET := $(filter OT_TARGET_ANNOTATION_%, $(.VARIABLES))

# go for it!
pipeline: | download_data_raw

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