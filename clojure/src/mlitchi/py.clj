(ns mlitchi.py)

;; Demonstrations-only namespace

(comment

  (ns-interns 'py.transformers)
  (ns-interns 'py.hfhub)
  (ns-interns 'py.hfhub-api)
  (ns-interns 'py.mlitchi)
  (ns-interns 'py.spacy)

  (py.hfhub-api/repo-type-and-id-from-hf-id "facebook/bart-large")
  (py.hfhub-api/hf-hub-url "facebook/bart-large" "config.json")
  (require '[clojure.repl :refer [doc]])
  (doc py.hfhub-api/hf-hub-url)
  (doc py.hfhub-api/repo-type-and-id-from-hf-id)
  (doc py.transformers/load-model)

  (py.transformers/get-device)
  (py.hfhub/url-of "facebook/bart-large" "config.json")
  (py.hfhub/model-info "facebook/bart-large" true)

  (def ^Boolean result (py.transformers/load-model "facebook/bart-large"))

  (->> "I am going to Mumbai this Saturday."
       py.spacy/nlp)

  (->> "I am going to Navi Mumbai in my car this Sunday."
       py.spacy/nlp-noun-chunks
       (map :root))

  (py.spacy/nlp-noun-chunks
    "It occurs sometimes, that you type in the REPL an expression that generated sequence like (range). In the usual REPL, it will display an infinite sequence of numbers on the screen until you quit the REPL. What a frustration! Now you have to start your REPL session over again.")
  )

