(ns mlitchi.core
  (:require [babashka.pods :as pods]))

(def pod-file "./mlitchi/pod.py")

(defn load-pod []
  (pods/load-pod pod-file)
  (require 'py.hf-api))

(defn unload-pod []
  (pods/unload-pod pod-file))

(comment

  (unload-pod)
  (load-pod)

  (py.hf-api/repo-type-and-id-from-hf-id "facebook/bart-large")
  (py.hf-api/hf-hub-url "facebook/bart-large" "config.json")

  (->> "I am going to Mumbai this Saturday."
       py.spacy/nlp
       :entities
       (map (fn [{:keys [text label]}] [text label])))
)
