(ns order-history.spec
  "Shape and format specifications that can be used with
  fooheads.rax.tree to convert relational data to a tree structure.")

(def orders-by-customer-tree
  [{:id :customers/id
    :name :customers/name
    :orders [{:id       :orders/id
              :status   :orders/status
              :shipment :orders/shipment-state
              :payment  :orders/payment-state
              :created  :orders/created-at
              :updated  :orders/updated-at
              :items    [{:sku      :items/sku
                          :name     :items/name
                          :price    :items/price
                          :quantity :items/quantity}]}]}])

(def item-by-customer-tree
  [{:id :customers/id
    :name :customers/name
    :items [{:sku      :items/sku
             :name     :items/name
             :orders [{:id       :orders/id
                       :created  :orders/created-at
                       :price    :items/price}]}]}])
