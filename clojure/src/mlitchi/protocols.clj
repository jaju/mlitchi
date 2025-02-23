(ns mlitchi.protocols)

(defprotocol Jsonable
  (to-json [this]))
