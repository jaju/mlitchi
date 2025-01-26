(ns mlitchi.deepseek
  (:require [wkok.openai-clojure.api :as api]))

(defonce ^:private -base-url "https://api.deepseek.com")
(defonce ^:private -beta-base-url "https://api.deepseek.com/beta")
(defonce ^:private -model-chat "deepseek-chat")
(defonce ^:private -model "deepseek-reasoner")

(defonce ^:private -api-key (atom nil))
(defn- api-key [] @-api-key)
(defn api-key! [k] (reset! -api-key k))

(defn- api-config-options []
  {:api-key      (api-key)
   :api-endpoint -base-url})

(defn- api-config-options-beta []
  {:api-key      (api-key)
   :api-endpoint -beta-base-url})


(comment
  (api/create-completion {:model  -model-chat
                          :prompt "I am going to Mumbai tomorrow and"}
                         (api-config-options-beta))

  (api/create-chat-completion {:model    -model
                               :messages [{:role    "user"
                                           :content "Choose a random number between 3 and -2"}]}
                              (api-config-options-beta))

  ;; Deepseek does not seem to have the embeddings API
  #_(api/create-embedding {:model "deepseek-reasoner"
                           :input "Why would one by Apple Silicon laptops?"}
                          (api-config-options))
  (api/list-models (api-config-options))
  (api/list-models (api-config-options-beta))
  )
