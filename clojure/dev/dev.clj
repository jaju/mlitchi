(ns dev
  (:require [nrepl.server :refer [start-server]]
            [clojure.tools.cli :as cli]
            [klipse-repl.main :as klipse]
            [mlitchi.core]
            [mount.core]))

(def cli-options
  [
   ["-p" "--port PORT" "port number"
    :default 10305
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ["-i" "--interactive" "run interactive REPL"
    :default false]
   ["-c" "--config-file FILE" "configuration file in EDN format"]])

(defonce nrepl-server (atom nil))

(defn start-nrepl-server [{:keys [port interactive]}]
  (reset! nrepl-server (start-server :port port))
  (println (str "nREPL server started on port " port))
  (if interactive
    (klipse/-main)
    @(promise)))

(defn -main [& args]
  (let [opts (cli/parse-opts args cli-options)
        options (:options opts)
        {:keys [port interactive config-file]} options]
    (#'mlitchi.core/init! config-file)
    (start-nrepl-server {:port port :interactive interactive})))
