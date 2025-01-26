(ns mlitchi.config
  (:require [clojure.java.io :as jio]
            [aero.core :refer [read-config]]))

(defonce -default-config-file "mlitchi-config.edn")

(defn -load-config [config-file]
  (let [config-file (jio/resource config-file)
        config (read-config config-file)]
    (println config)
    config))

(defonce -config (atom {}))

(defn load-config!
  ([] (load-config! -default-config-file))
  ([config-file]
   (reset! -config (-load-config config-file))
   @-config))

(defn config [] @-config)
(defn pod-file []
  (:pod-file (config)))
(defn http-port []
  (:http-port (config)))
