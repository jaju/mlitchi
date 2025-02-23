(ns mlitchi.gemini
  (:require [cheshire.core :as json]
            [hato.client :as http]
            [mlitchi.config :as config]
            [mlitchi.gemini
             [types :as types]
             [helpers :as helpers]]
            [mlitchi.protocols :refer [to-json]]
            [mlitchi.jvm-utils :refer [declared-fields]])
  (:import [clojure.lang Reflector]
           [com.google.genai ApiClient Client Models HttpApiClient]))

(defrecord ModelAPI [^String api-version ^String model-name])
(defonce ^:private -api-key (atom nil))

(def ^String generative-language-endpoint-base "https://generativelanguage.googleapis.com")
(defn- endpoint-base-for [^ModelAPI model-api]
  (let [{:keys [api-version model-name]} model-api]
    (str generative-language-endpoint-base "/" api-version "/models/" model-name)))

(defn- api-key [] @-api-key)
(defn api-key! [k] (reset! -api-key k))

(defn- ^Client create-client []
  (-> (Client/builder)
      (.apiKey (api-key))
      .build))

(defn ^String generate-content-endpoint
  ([model-api]
   (generate-content-endpoint model-api (api-key)))
  ([model-api api-key]
   (str (endpoint-base-for model-api) ":generateContent?key=" api-key)))

(defonce ^ModelAPI default-model (->ModelAPI "v1beta" "gemini-2.0-flash"))
(defn generate-content [^Client client ^String text]
  (let [^Models m (.models client)
        response (.generateContent m ^String (:model-name default-model) text nil)]
    (json/generate-string (.text response))))

(defn generate-chat-request-body
  ([system-prompt user-input]
   (generate-chat-request-body system-prompt user-input []))
  ([system-prompt user-input history]
   (let [contents (or history [])
         contents (conj contents {:role "user" :parts {:text user-input}})]
     (json/generate-string
       {
        :system_instruction
        {:role  "system"
         :parts {:text system-prompt}}
        :contents contents}))))

(defn chat-generate [model system-prompt ^String user-input & [history]]
  (let [end-point (generate-content-endpoint model)
        history (or history [])
        contents (conj history {:role "user" :parts {:text user-input}})]
    (http/post end-point
               {:as      :json
                :headers {"Content-Type" "application/json"}
                :body    (generate-chat-request-body system-prompt user-input history)})))

(comment
  (defonce client (create-client))
  (declared-fields Client)
  (declared-fields client)
  (.models client)
  (helpers/declared-fields client)
  )
