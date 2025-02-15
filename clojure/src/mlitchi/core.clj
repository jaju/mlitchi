(ns mlitchi.core
  (:require [babashka.pods :as pods]
            [clojure.tools.cli :as cli]
            [mount.core :refer [defstate]]
            [mlitchi.config :as mlitchi-config]
            [mlitchi.deepseek :as deepseek]
            [mlitchi.gemini :as gemini]
            [mlitchi.azure :as azure]
            [mlitchi.ollama :as ollama]))

(defn- load-pod [pod-file]
  (println "Loading pod file...")
  (pods/load-pod pod-file))

(defn- unload-pod [pod-id]
  (pods/unload-pod pod-id))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defstate core-state
          :start (do
                   (ollama/init-config! (mlitchi-config/ollama))
                   (load-pod (mlitchi-config/pod-file)))
          :stop (do
                  (unload-pod core-state)))

(def cli-options
  [["-c" "--config-file FILE" "configuration file in EDN format"
    :default "mlitchi-config.edn"]])

(defn- init! [& [config-file]]
  (if config-file
    (mlitchi-config/load-config! config-file)
    (mlitchi-config/load-config!))
  (let [global-config (mlitchi-config/config)]
    (if-let [deepseek-config (:deepseek global-config)]
      (deepseek/api-key! (:api-key deepseek-config)))
    (if-let [gemini-config (:gemini global-config)]
      (gemini/api-key! (:api-key gemini-config)))
    (if-let [azure-config (:azure global-config)]
      (azure/init! azure-config)))
  (mount.core/start))

;; Dormant function. To be evolved once I set up the jar packaging to run
;; Until then, dev.clj is the entry point to the application
(defn -main [& args]
  (let [opts (cli/parse-opts args cli-options)
        options (:options opts)
        {:keys [config-file]} options]
    (init! config-file)))
