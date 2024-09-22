(ns mlitchi.core
  (:require [babashka.pods :as pods]))

(def pod-file "./mlitchi/pod.py")

(defn load-pod [pod-file]
  (pods/load-pod pod-file))

(defn unload-pod [pod-file]
  (pods/unload-pod pod-file))

(defn reload-pod [pod-file]
  (unload-pod pod-file)
  (load-pod pod-file))

(comment

  (reload-pod pod-file)

  (py.hfhub-api/repo-type-and-id-from-hf-id "facebook/bart-large")
  (py.hfhub-api/hf-hub-url "facebook/bart-large" "config.json")

  (py.transformers/get-device)

  (->> "I am going to Mumbai this Saturday."
       py.spacy/nlp)

  (->> "I am going to Navi Mumbai in my car this Sunday."
       py.spacy/nlp-noun-chunks)
  )
