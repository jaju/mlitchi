(ns mlitchi.core
  (:require [babashka.pods :as pods]
            [clojure.tools.cli :as cli]
            [mount.core :refer [defstate]]
            [mlitchi.config :as mlitchi-config]))

(defn- load-pod [pod-file]
  (println "Loading pod file...")
  (pods/load-pod pod-file))

(defn- unload-pod [pod-id]
  (pods/unload-pod pod-id))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defstate core-state
          :start (load-pod (mlitchi-config/pod-file))
          :stop (unload-pod core-state))

(def cli-options
  [["-c" "--config-file FILE" "configuration file in EDN format"
    :default "mlitchi-config.edn"]])

(defn- init! [& [config-file]]
  (if config-file
    (mlitchi-config/load-config! config-file)
    (mlitchi-config/load-config!))
  (mount.core/start))

;; Dormant function. To be evolved once I set up the jar packaging to run
;; Until then, dev.clj is the entry point to the application
(defn -main [& args]
  (let [opts (cli/parse-opts args cli-options)
        options (:options opts)
        {:keys [config-file]} options]
    (init! config-file)))
