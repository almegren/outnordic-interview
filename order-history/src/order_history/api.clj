(ns order-history.api
  (:require
   [honey.sql :as sql]
   [fooheads.rax.tree :as tree]
   [next.jdbc :as jdbc]
   [order-history.queries :as queries]
   [order-history.spec :as spec]
   [order-history.config :as config]
   [order-history.stdlib :refer [map-vals parse-int ->kebab-case map-keys]]))


(def db (jdbc/get-datasource
          (:db  ;; Get then database config from the current config
            (config/get-config :dev)))) ;; Get the config for dev env

(defn execute-query!
  "Executes a preformatted sql query and returns the relation result."
  [db query]
  (->>
    query
    (jdbc/execute! db)
    (map #(map-keys ->kebab-case %))))

(defn reshape
  "Reshapes relational data to a tree structure."
  [rel shape]
  (tree/rel->tree rel shape))

(defn wrap-result
  [result]
  (if (seq result)
    {:status 200 :body result}
    {:status 400 :body "Customer or item not found"}))

(defn create-handler
  [query-fn params shape]
  (fn [{:keys [path-params]}]
    (try
      (let [parsed-params
            ;; Parse parameters based on type.
            (->>
              params
              (map (fn [[k t]]
                     [k (case t
                          :int
                          (parse-int (get path-params k))
                          (get path-params k))]))
              (into {}))

            sql-query
            ;; Format a sql query using honeysql
            (sql/format (query-fn parsed-params))

            rel
            (execute-query! db sql-query)

            formatted-output
            ;; Reshape output using fooheads.rax.tree to get a tree shape
            (reshape rel shape)]

         (wrap-result formatted-output))

      (catch Exception e
        (do
          (.printStackTrace e)
          (throw e))))))

(def customer-order-handler
  (create-handler queries/get-orders-by-customer-honey
                  {:customer-id :int}
                  spec/orders-by-customer-tree))

(def customer-item-handler
  (create-handler queries/get-item-orders-by-customer-honey
                  {:customer-id :int
                   :item-sku    :string}
                  spec/item-by-customer-tree))
