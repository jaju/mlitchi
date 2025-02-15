(ns mlitchi.gemini.types
  (:import [clojure.lang Reflector]
           [com.google.genai JsonSerializable]
           [com.google.genai.types
            GenerationConfig
            Content
            Part
            SafetySetting]))

(defprotocol Jsonable
  (to-json [this]))
(extend-protocol Jsonable
  JsonSerializable
  (to-json [^JsonSerializable this]
    (.toJson this)))

(defmacro ^:private build-instance [class-name kv-map]
  `(let [builder# (. ~class-name builder)]
     (doseq [[k# v#] ~kv-map]
       (Reflector/invokeInstanceMethod builder# (name k#) (to-array [v#])))
     (.build builder#)))

(defmacro ^:private builder-fn [class-name]
  `(fn [kv-map#]
     (build-instance ~class-name kv-map#)))

(def generation-config (builder-fn GenerationConfig))
(def part (builder-fn Part))
(def content (builder-fn Content))


(comment

  (.toJson (build-instance Part {:text "Hello World"}))
  (. Part builder)
  (-> (Part/builder)
      (.text "Hello, world!")
      .build
      .toJson)
  (to-json
    (generation-config {}))

  (to-json (content {:parts [(part {:text "Hello, world!"})
                             (part {:text "How are you?"})]}))

  )
