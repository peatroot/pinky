(ns dev-resources.user
  (:require
    [pinky-api.schema :as s]
    [com.walmartlabs.lacinia :as lacinia]))

(def schema (s/load-schema))

(defn q
  [query-string]
  (lacinia/execute schema query-string nil nil))