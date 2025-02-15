(ns mlitchi.gemini
  (:require [cheshire.core :as json]
            [hato.client :as http]
            [mlitchi.config :as config])
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

(defn debug-fields [client]
  (doseq [field (.getDeclaredFields (if (instance? Class client) client (class client)))]
    (.setAccessible field true)
    (println (.getName field))))

(defn- get-private-field [object field & [clzz]]
  (let [field (.getDeclaredField (or clzz (class object)) field)]
    (.setAccessible field true)
    (.get field object)))

(defn get-api-client [client]
  (let [field (.getDeclaredField (class client) "apiClient")]
    (.setAccessible field true)
    (.get field client)))


(comment
  (defonce client (create-client))
  (debug-fields client)
  (debug-fields Client)
  (def api-client (get-private-field client "apiClient"))
  (class api-client)
  (debug-fields api-client)
  (.getDeclaredFields ApiClient)
  (.getDeclaredFields HttpApiClient)
  (debug-fields ApiClient)
  (debug-fields ^ApiClient HttpApiClient)
  (def http-options (get-private-field api-client "httpOptions" ApiClient))

  (.get (.baseUrl http-options))
  (-> http-options .baseUrl .get)
  (-> http-options .headers .get)
  (-> http-options .apiVersion .get)
  (Reflector/getInstanceField client "apiClient")
  (Reflector/getInstanceField client (name :apiClient))
  (.models client)
  )
