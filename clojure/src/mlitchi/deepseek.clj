(ns mlitchi.deepseek
  (:require [wkok.openai-clojure.api :as api]
            [hato.client :as hc]
            [cheshire.core :as json]))

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

(defn get-balance []
  (let [url (str -base-url "/user/balance")
        headers {"Authorization" (str "Bearer " (api-key))}]
    (-> url
        (hc/get {:headers headers})
        :body
        (json/parse-string keyword))))

(defn context->create-completion [context & [model]]
  (fn [text]
    (let [prompt (str context "\n" text)]
      (api/create-completion {:model  (or model -model-chat)
                              :prompt prompt}
                             (api-config-options-beta)))))

(defn context->create-chat-completion [system-context & [model]]
  (let [context (if (string? system-context)
                  [{:role "system" :content system-context}]
                  system-context)]
    (fn [text-or-conversation]
      (let [prompt (if (string? text-or-conversation)
                     [{:role "user" :content text-or-conversation}]
                     text-or-conversation)
            prompt (concat context prompt)]
        (api/create-chat-completion {:model    (or model -model-chat)
                                     :messages prompt}
                                    (api-config-options-beta))))))

(comment

  (get-balance)

  (api/create-completion {:model  -model
                          :prompt "I am going to Mumbai tomorrow and"}
                         (api-config-options-beta))

  (api/create-chat-completion {:model    -model-chat
                               :messages [{:role    "user"
                                           :content "Choose a random number between 3 and -2"}]}
                              (api-config-options-beta))

  ;; Deepseek does not seem to have the embeddings API
  #_(api/create-embedding {:model "deepseek-reasoner"
                           :input "Why would one by Apple Silicon laptops?"}
                          (api-config-options))
  (api/list-models (api-config-options))
  (api/list-models (api-config-options-beta))

  (def chatter (context->create-chat-completion [{:role    "system"
                                                  :content "You are an expert Javascript programmer and can automate MacOS using osascript with Javascript using JXA. The user is also an expert programmer. The user is looking to automate certain workflows. The user will describe their requirement in plain text. You will generate self-contained osascript commands that satisfy the requirement."}
                                                 {:role    "user"
                                                  :content "I need a script to simply append any text in the system's paste buffer to be appended to a file named capture.txt"}
                                                 {:role    "assistant"
                                                  :content "osascript -l JavaScript -e '\nvar app = Application.currentApplication();\napp.includeStandardAdditions = true;\n\nvar clipboardContent = app.theClipboard(); // Get clipboard contents\nvar filePath = app.pathTo(\"/Users/jaju\") + \"/capture.txt\"; // Get home directory path\n\n// Append to the file with a newline\nvar file = app.openForAccess(Path(filePath), { writePermission: true });\napp.setPositionToEndOfFile(file); // Move to end of file\napp.write(clipboardContent + \"\\n\", { to: file });\napp.closeAccess(file);\n'"}]))

  (def programmer
    (context->create-completion
      "<context>You are an expert programming Al assistant who prioritizes minimalist, efficient code in Clojure.
  You plan before coding, write idiomatic solutions, seek clarification when needed, and accept user preferences even if suboptimal.</context>
  <planning_rules>
  - Create 3-step numbered plans before coding
  - Display current plan step clearly
  - Ask for clarification on ambiguity
  - Optimize for minimal code and overhead
  </planning_rules>
  <format_rules>
  - Use code blocks for simple task. Use core clojure functions only.
  - Split long code into section
  - Create artifacts for file-level tasks
  - Keep responses brief but complete
  </format_rules>
  OUTPUT: Create responses following these rules.
  Focus on minimal, efficient solutions while maintaining a helpful, concise style."))

  (chatter "Give me a script to output the name of the currently focused application window.")

  (programmer "Write a program to create a sequence of all Fibonacci numbers until n, the input parameter.")

  (programmer "Write a function that reverses a string. Capitalize the string.")

  (def ne-extractor
    (context->create-chat-completion
      "<context>You are a helpful assistant who helps organize content for intelligent actions and precise retrievals.
The way you do it is, for any user-supplied text, you analyse it for the presence of various entities, and respond in JSON.</context>
<extraction_rules>
- Be tolerant towards style, case, spelling and grammatical errors.
- If you are not very confident, indicate so in the output, in a field.
- Identify all entities, like named entities, measurements with units, geographical places, time.
- Identify the topic of the input.
</extraction_rules>
<format_rules>
- Use JSON for the output.
- Have a field named \"topic\"
- Have a field named entities, which is an array containing entity objects, each with their type information
- Have a field named confidence, whose value is one of LOW, MEDIUM, HIGH - depending on how comfortable you are with your own analysis.
</format_rules>
OUTPUT: It should be pure, valid JSON ONLY."
      -model-chat))

  (ne-extractor "I have been attending an online course on Machine Learning which focuses on the fundamental mathematics of ML. The lectures happen every Saturday at 7 PM, for 2 hours.")

  (ne-extractor "uv synchronizes based on the pyproject.toml. By declaring en_core_web_sm explicitly as a dependency using its wheel URL, uv recognizes it and won’t uninstall it during syncs. This adheres to clean software process principles without relying on post-install hacks.")

  (ne-extractor "The Prime Minister of India, on a tour to Maldives, realized the importance of cleanliness for growing tourism, and set a target of a year for five tourist cities with beaches to be developed as model tourism spots in India.")

  )
