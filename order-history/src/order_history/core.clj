(ns order-history.core
  (:gen-class)
  (:require
   [order-history.seed :as seed]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (seed/seed-db!))
