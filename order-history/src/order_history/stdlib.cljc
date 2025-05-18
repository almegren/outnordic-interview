(ns order-history.stdlib
  (:require
    [camel-snake-kebab.core :as csk]))


(defn parse-int
  "Returns int (clj) or number (cljs) given a string, or nil if not parseable.
  A string is parseable if it leads with a `+` or `-` and follows either a
  zero or a non-zero and zero or more digits."
  [s]
  (when (re-matches #"[-+]?(?:0|[1-9]\d*)" s)
    #?(:clj (java.lang.Integer/parseInt s 10)
       :cljs (js/parseInt s 10))))


(defn ->kebab-case
  [val]
  (if (keyword? val)
    (let [s (namespace val)
          n (name val)]
      (keyword (csk/->kebab-case s)
               (csk/->kebab-case n)))
    (csk/->kebab-case val)))


(defn map-keys
  "Apply f to all keys in m"
  [f m]
  (reduce-kv (fn [m k v] (assoc m (f k) v)) {} m))


(defn map-vals
  "Apply f to all values in m."
  [f m]
  (reduce-kv
    (fn [m k v]
      (assoc m k (f v))) {} m))


