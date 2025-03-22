(ns mlitchi.jvm-utils)

(defn declared-fields [clazz-or-object]
  (for [field (.getDeclaredFields 
                (if (instance? Class clazz-or-object) 
                  clazz-or-object 
                  (class clazz-or-object)))]
    (do
      (.setAccessible field true)
      (.getName field))))

(comment
  (declared-fields com.google.genai.ApiClient))

