(ns mlitchi.azure
  (:require [wkok.openai-clojure.api :as api]
            [mlitchi.config :as config]))

(defonce ^:private -api-key (atom nil))
(defonce ^:private -api-endpoint (atom nil))
(defn- api-key [] @-api-key)
(defn- api-endpoint [] @-api-endpoint)
(defn api-key! [k] (reset! -api-key k))
(defn api-endpoint! [k] (reset! -api-endpoint k))

(defn init! [{:keys [api-key api-endpoint] :as config}]
  (api-key! api-key)
  (api-endpoint! api-endpoint))

(defn create-chat-completion [model messages]
  (api/create-chat-completion {:model    model
                               :messages messages}
                              {:api-key      (api-key)
                               :api-endpoint (api-endpoint)
                               :impl         :azure}))
