(ns repl.core
  (:require [nrepl.server :refer [start-server]]
            [cider.nrepl :refer [cider-nrepl-handler]]
            [klipse-repl.main :as klipse]))

(defonce nrepl-server (atom nil))

(defn start-nrepl-server [{:keys [port]}]
  (reset! nrepl-server (start-server :port port))
  (println (str "nREPL server started on port " port))
  (klipse-repl.main/-main))
