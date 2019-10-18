// creates a minimal json file from the preliminary locus-to-gene score
val inFileL2g = sys.env("OT_GENETICS_ANALYSIS_L2G")
val outFileG2v = sys.env("FILE_LOCUS_2_GENE")

// load the locus to gene data
val l2g = spark.read.parquet("data/raw/" + inFileL2g)

// extract just the g2v part (ignore studies)
val g2v = l2g.select(
    l2g.col("gene_id").alias("ensgId"),
    l2g.col("study_id").alias("studyId"),
    l2g.col("efo_codes_str").alias("efoId"),
    concat(
        l2g.col("chrom"),
        lit("_"),
        l2g.col("pos"),
        lit("_"),
        l2g.col("ref"),
        lit("_"),
        l2g.col("alt")
    ).alias("variantId"),
    l2g.col("postprob_all_features").alias("postProb"),
    l2g.col("otp_overall_score").alias("score")
).na.drop().dropDuplicates()

// write out
g2v.coalesce(1).write.mode("overwrite").json("data/raw/" + outFileG2v)

// exit (assuming usage with spark-shell -i)
System.exit(0)
