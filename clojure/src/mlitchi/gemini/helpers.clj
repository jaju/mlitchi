(ns mlitchi.gemini.helpers
  (:import [clojure.lang Reflector]
           [com.google.genai ApiClient Client Models HttpApiClient]))

(defn- get-private-field [object field & [clzz]]
  (let [field (.getDeclaredField (or clzz (class object)) field)]
    (.setAccessible field true)
    (.get field object)))

(defn get-api-client [^Client client]
  (let [field (.getDeclaredField (class client) "apiClient")]
    (.setAccessible field true)
    (.get field client)))

(defn- get-http-options [^ApiClient api-client]
  (let [http-options (get-private-field api-client "httpOptions" ApiClient)]
    {:base-url    (-> http-options .baseUrl .get)
     :headers     (-> http-options .headers .get)
     :api-version (-> http-options .apiVersion .get)}))

(comment
  (def api-client (get-private-field client "apiClient"))
  (.getDeclaredFields ApiClient)
  (.getDeclaredFields HttpApiClient)

  (declared-fields ApiClient)
  (declared-fields ^ApiClient HttpApiClient)
  (def http-options (get-private-field api-client "httpOptions" ApiClient))
  (.get (.baseUrl http-options))
  (-> http-options .baseUrl .get)
  (-> http-options .headers .get)
  (-> http-options .apiVersion .get)
  (Reflector/getInstanceField client "apiClient")
  (Reflector/getInstanceField client (name :apiClient)))
