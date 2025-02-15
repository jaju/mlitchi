(ns mlitchi.config
  (:require [clojure.java.io :as jio]
            [aero.core :refer [read-config]]
            [clojure.edn :as edn]))

;; Constants, defaults, state
(defonce -config (atom {}))
(defonce -default-config-file "mlitchi-config.edn")

;; Helpers
(defn- -read-config [config-file]
  "Load as resource, so the file can be loaded in case where the path is relative, absolute, or in resources classpath"
  (let [config-file (jio/resource config-file)
        config (read-config config-file)]
    config))

;; Public

(defn load-config!
  "Loads configuration from and EDN file. Stateful. Call once to load from EDN config file."
  ([] (load-config! -default-config-file))
  ([config-file]
   (reset! -config (-read-config config-file))
   @-config))


(defn config []
  "The current complete configuration"
  @-config)

;; Pod
(defn pod-file []
  (:pod-file (config)))

;; Network
(defn http-port []
  (:http-port (config)))

;; Ollama configuration
(defn ollama []
  (:ollama (config)))

(defn prompts []
  (let [prompts-file (:prompt-library (config))]
    (edn/read-string (slurp prompts-file))))
