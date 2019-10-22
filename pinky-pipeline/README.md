# pinky-pipeline

A basic pipeline for generating input files for `pinky-api` from a subset of Open Targets data. Could be dramatically improved.

## Usage

You will need access to several Open Targets google storage buckets. You will need `jq@1.6` and `spark@2.4.3` installed.

To download the relevant files (see `Makefile` and `.env` for remote file locations), run:

```
make download_raw_data
```

To run the pipeline, run:

```
make create_entities
```
