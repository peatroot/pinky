(ns pinky-api.loader
  (:require
    [clojure.java.io :as io]
    [cheshire.core :as json])
  (:import java.util.zip.GZIPInputStream))

; loader utils
(defn- load-lines-from-resource-file
  [filename] 
  (with-open
    [rdr (io/reader (java.util.zip.GZIPInputStream. (io/input-stream (io/resource filename))))]
    (into-array (line-seq rdr))))
(defn- parse-json-as-keywords
  [line]
  (json/parse-string line true))
(defn load-json-lines 
  [filename]
  (doall (map parse-json-as-keywords (load-lines-from-resource-file filename))))