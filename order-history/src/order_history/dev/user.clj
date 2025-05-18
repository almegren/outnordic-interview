(ns user)


(comment
  (->
    seed/db-spec
    (jdbc/execute! ["select * from customers"])
    (clojure.pprint/pprint))

  (->
    seed/db-spec
    (jdbc/execute! ["select * from orders"])
    (clojure.pprint/pprint))

  (->
    seed/db-spec
    (jdbc/execute! ["select * from items"])
    (clojure.pprint/pprint))


  (->
    seed/db-spec
    (jdbc/execute! ["select * from customers"])
    (first)
    (keys)
    (clojure.pprint/pprint)))


(def db-tables
  [[:items/sku
    :items/order_id
    :items/name
    :items/price
    :items/quantity]

   [:orders/id
    :orders/customer_id
    :orders/status
    :orders/shipment_state
    :orders/payment_state
    :orders/created_at
    :orders/updated_at]

   [:customers/updated_at
    :customers/created_at
    :customers/postalcode
    :customers/country
    :customers/address
    :customers/phone
    :customers/city
    :customers/name
    :customers/email
    :customers/id]])


(comment
  (->
    seed/db-spec
    (jdbc/execute!
     ["select * from customers
    join orders on customers.id = orders.customer_id
    join items on orders.id = items.order_id
    where customer_id = ?"
      1])
    (->> (map #(map-keys ->kebab-case %)))
    ;(normalize-result-set)
    (tree/rel->tree
      [{:id :customers/id
        :name :customers/name
        :orders [{:id :orders/id
                  :status   :orders/status
                  :shipment :orders/shipment-state
                  :payment  :orders/payment-state
                  :created  :orders/created-at
                  :updated  :orders/updated-at
                  :items [{:sku      :items/sku
                           :name     :items/name
                           :price    :items/price
                           :quantity :items/quantity}]}]}])))


(comment
  (get-orders-by-customer 1))

