(ns order-history.config)

(def config
  {:dev {:db {:dbtype "postgresql"
              :dbname "order-history"
              :host "localhost"
              :port 5432
              :user "test"
              :password "test1234"}}})

(defn get-config
  [_env]
  (:dev config))
  
