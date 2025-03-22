(ns mlitchi.ollama
  (:require [hato.client :as hc]
            [cheshire.core :as json]))

(defonce -config (atom {}))
(defn init-config! [ollama-config]
  (reset! -config ollama-config))

(defn base-url []
  (:url @-config))

(defn- generator-url []
  (str (base-url) "/api/generate"))

(defn generate [model prompt]
  (let [body {:model model
              :prompt prompt
              :stream false}]
    (hc/post (generator-url) {:body (json/generate-string body) :content-type :json})))

(comment
  (def text1 "What is the capital of India?")

  (def text2 "Tell me more about the SOLID principles.")

  (def model 
    "gemma3"
    ; "hf.co/unsloth/DeepSeek-R1-Distill-Llama-8B-GGUF:Q4_K_M"
    ; "qwq"
    )

  (def
    result
    (generate
    model
    text2))
  (keys result)
  (-> result
      :body
      (json/parse-string keyword)
      :response)
  )
