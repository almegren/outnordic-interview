(ns order-history.queries
  "Queries written honey-sql data, either directly or using the dsl."
  (:require
    [honey.sql.helpers :refer [select from join where]]))

(defn get-orders-by-customer-honey
  [{:keys [customer-id]}]
  (->
    (select :*)
    (from :customers)
    (join :orders [:= :customers.id :orders.customer-id])
    (join :items [:= :orders.id :items.order-id])
    (where [:= :customers.id customer-id])))

(defn get-item-orders-by-customer-honey
  [{:keys [customer-id item-sku]}]
  (->
    (select :*)
    (from :customers)
    (join :orders [:= :customers.id :orders.customer-id])
    (join :items [:= :orders.id :items.order-id])
    (where [:and
            [:= :items.sku item-sku]
            [:= :customers.id customer-id]])))
