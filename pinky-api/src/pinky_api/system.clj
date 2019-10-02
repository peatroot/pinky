(ns pinky-api.system
  (:require
    [com.stuartsierra.component :as component]
    [pinky-api.schema :as schema]
    [pinky-api.server :as server]))

(defn new-system
  []
  (merge (component/system-map)
         (server/new-server)
         (schema/new-schema-provider)))