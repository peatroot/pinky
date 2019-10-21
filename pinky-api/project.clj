(defproject pinky-api "0.1.0-SNAPSHOT"
  :description "A GraphQL API exposing biological fact inference"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [com.stuartsierra/component "0.3.2"]
                 [com.walmartlabs/lacinia-pedestal "0.5.0"]
                 [io.aviso/logging "0.2.0"]
                 [com.cerner/clara-rules "0.19.1"]
                 [cheshire "5.9.0"]]
  :main ^:skip-aot pinky-api.core
  :target-path "target/%s"
  :jvm-opts ["-Xmx8G"]
  :profiles {:uberjar {:aot :all}}
  :repl-options {:init-ns dev-resources.user})
