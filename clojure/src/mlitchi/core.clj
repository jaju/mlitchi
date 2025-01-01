(ns mlitchi.core
  (:require [babashka.pods :as pods]))

(def pod-file "../python/pod.py")

(defn load-pod [pod-file]
  (pods/load-pod pod-file))

(defn unload-pod [pod-file]
  (pods/unload-pod pod-file))

(defn reload-pod [pod-file]
  (unload-pod pod-file)
  (load-pod pod-file))

(comment

  (reload-pod pod-file)

  (ns-interns 'py.transformers)
  (ns-interns 'py.hfhub)
  (ns-interns 'py.hfhub-api)
  (ns-interns 'py.mlitchi)

  (py.hfhub-api/repo-type-and-id-from-hf-id "facebook/bart-large")
  (py.hfhub-api/hf-hub-url "facebook/bart-large" "config.json")
  (require '[clojure.repl :refer [doc]])
  (doc py.hfhub-api/hf-hub-url)
  (doc py.hfhub-api/repo-type-and-id-from-hf-id)
  (doc py.transformers/load-model-and-tokenizer)


  (py.transformers/get-device)
  (py.hfhub/url-of "facebook/bart-large" "config.json")
  (py.hfhub/model-info "facebook/bart-large" true)

  (def ^Boolean result (py.transformers/load-model-and-tokenizer "facebook/bart-large"))

  (->> "I am going to Mumbai this Saturday."
       py.spacy/nlp)

  (->> "I am going to Navi Mumbai in my car this Sunday."
       py.spacy/nlp-noun-chunks)
  )
