#!make

# load environment variables
include .env
export

FILES_EVIDENCE := $(filter OT_EVIDENCE_%, $(.VARIABLES))
FILES_ANNOTATION_TARGET := $(filter OT_TARGET_ANNOTATION_%, $(.VARIABLES))
FILES_STRINGDB := STRINGDB_INFO STRINGDB_LINKS

FILE_ENTITY_GENE := gene.jsonl.gz
FILE_PPIS := protein-protein-interactions.jsonl.gz
FILE_MECHANISM_OF_ACTION := mechanism-of-action.jsonl.gz

# LUTs
FILE_LUT_STRINGDB_2_SYMBOL := stringdb-2-symbol.json
FILE_LUT_UNIPROT_2_ENSEMBL := uniprot-2-ensembl.json
FILE_LUT_ENSEMBL_2_UNIPROT := ensembl-2-uniprot.json
FILE_LUT_SYMBOL_2_ENSEMBL := symbol-2-ensembl.json

# go for it!
pipeline: create_entities

create_entities: data/processed/$(FILE_ENTITY_GENE) data/processed/$(FILE_PPIS) data/processed/$(FILE_MECHANISM_OF_ACTION)

# build gene entity
data/processed/$(FILE_ENTITY_GENE): data/raw/$(OT_TARGET_ANNOTATION_HGNC) # | download_data_raw
	jq -c '.response.docs[] | select(.ensembl_gene_id) | {hgncId: .hgnc_id, ensgId: .ensembl_gene_id, symbol: .symbol, name: .name}' $< | gzip > $@

# build protein-protein entity relationships
data/processed/$(FILE_PPIS): data/raw/$(STRINGDB_LINKS) data/raw/$(FILE_LUT_STRINGDB_2_SYMBOL) data/raw/$(FILE_LUT_SYMBOL_2_ENSEMBL) | download_data_raw
	gzcat $< | jq --argfile stringDb2Symbol data/raw/$(FILE_LUT_STRINGDB_2_SYMBOL) --argfile symbol2Ensembl data/raw/$(FILE_LUT_SYMBOL_2_ENSEMBL) --raw-input --slurp -c 'split("\n") | map(split(" ")) | .[1:-1] | map({"gene1": $$symbol2Ensembl[0][$$stringDb2Symbol[0][.[0]]], "gene2": $$symbol2Ensembl[0][$$stringDb2Symbol[0][.[1]]], "interactionScore": (.[2] | tonumber)}) | .[]' | gzip > $@

# build drug mechanisms of action
data/processed/$(FILE_MECHANISM_OF_ACTION): data/raw/$(OT_EVIDENCE_CHEMBL) data/raw/$(FILE_LUT_UNIPROT_2_ENSEMBL)
	gzcat $< | head -n50 | jq --argfile uniprot2Ensembl data/raw/$(FILE_LUT_UNIPROT_2_ENSEMBL) --slurp -c 'map({"ensemblId": $$uniprot2Ensembl[0][.target.id | split("/")[-1]], "chemblId": (.drug.id | split("/")[-1]), "mechanismOfActionType": .evidence.target2drug.action_type, "mechanismOfAction": .evidence.target2drug.mechanism_of_action}) | unique_by(.ensemblId, .chemblId, .mechanismOfAction) | .[]' | gzip > $@

# build look up tables
data/raw/$(FILE_LUT_STRINGDB_2_SYMBOL): data/raw/$(STRINGDB_INFO) | download_data_raw
	gzcat $< | jq --raw-input --slurp '[split("\n") | map(split("\t")) | .[1:-1] | map({"stringId": .[0], "symbol": .[1]}) | reduce .[] as $$item ({}; .[$$item.stringId] = $$item.symbol)]' > $@
data/raw/$(FILE_LUT_UNIPROT_2_ENSEMBL): data/raw/$(OT_TARGET_ANNOTATION_HGNC) | download_data_raw
	cat $< | jq '[[.response.docs[] | select(.ensembl_gene_id and .uniprot_ids) | {"ensemblId": .ensembl_gene_id, "uniprotId": .uniprot_ids[0]}] | reduce .[] as $$item ({}; .[$$item.uniprotId] = $$item.ensemblId)]' > $@
data/raw/$(FILE_LUT_ENSEMBL_2_UNIPROT): data/raw/$(OT_TARGET_ANNOTATION_HGNC) | download_data_raw
	cat $< | jq '[[.response.docs[] | select(.ensembl_gene_id and .uniprot_ids) | {"ensemblId": .ensembl_gene_id, "uniprotId": .uniprot_ids[0]}] | reduce .[] as $$item ({}; .[$$item.ensemblId] = $$item.uniprotId)]' > $@
data/raw/$(FILE_LUT_SYMBOL_2_ENSEMBL): data/raw/$(OT_TARGET_ANNOTATION_HGNC) | download_data_raw
	cat $< | jq '[[.response.docs[] | select(.ensembl_gene_id and .symbol) | {"ensemblId": .ensembl_gene_id, "symbol": .symbol}] | reduce .[] as $$item ({}; .[$$item.symbol] = $$item.ensemblId)]' > $@

# download raw data
download_data_raw: $(FILES_ANNOTATION_TARGET) $(FILES_EVIDENCE) $(FILES_STRINGDB) | make_data_directory

$(FILES_EVIDENCE):
	gsutil -m cp -n $(OT_BUCKET_EVIDENCE)/$($@) data/raw/$($@)

$(FILES_ANNOTATION_TARGET):
	gsutil -m cp -n $(OT_BUCKET_ANNOTATION)/$($@) data/raw/$($@)

$(FILES_STRINGDB):
	wget -N -q -O data/raw/$($@) $($@_DIR)/$($@)

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