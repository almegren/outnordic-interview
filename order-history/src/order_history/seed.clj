(ns order-history.seed
  (:require
    [next.jdbc :as jdbc]
    [order-history.config :as config]))


(def db (jdbc/get-datasource
          (:db  ;; Get then database config from the current config
            (config/get-config :dev)))) ;; Get the config for dev env

(defn drop-customer-table []
  (jdbc/execute! db
                 ["DROP TABLE IF EXISTS customers"]))

(defn create-customer-table []
  (jdbc/execute! db
                 ["CREATE TABLE IF NOT EXISTS customers (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    email VARCHAR(255) NOT NULL,
                    phone VARCHAR(255) NOT NULL,
                    address VARCHAR(255) NOT NULL,
                    city VARCHAR(255) NOT NULL,
                    postalcode VARCHAR(255) NOT NULL,
                    country VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                  )"]))

(defn create-customer [name email phone address city postalcode country]
  (->
   (jdbc/execute-one! db
                 ["INSERT INTO customers (name, email, phone, address, city, postalcode, country) VALUES (?, ?, ?, ?, ?, ?, ?)
                   RETURNING id"
                  name email phone address city postalcode country])
   :customers/id))

(defn drop-orders-table []
  (jdbc/execute! db
                 ["DROP TABLE IF EXISTS orders"]))

(defn create-orders-table []
  (jdbc/execute! db
                 ["CREATE TABLE IF NOT EXISTS orders (
                    id SERIAL PRIMARY KEY,
                    customer_id INTEGER NOT NULL,
                    status VARCHAR(255) NOT NULL,
                    shipment_state VARCHAR(255) NOT NULL,
                    payment_state VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    CONSTRAINT fk_customer
                      FOREIGN KEY(customer_id)
	                       REFERENCES customers(id)
                  )"]))

(defn create-order [customer_id status shipment_state payment_state]
  (->
   (jdbc/execute-one! db
                      ["INSERT INTO orders (customer_id, status, shipment_state, payment_state) VALUES (?, ?, ?, ?)
                        RETURNING id"
                       customer_id status shipment_state payment_state])
   :orders/id))

(defn add-dummy-orders [customer_id]
  [(create-order customer_id "open" "shipped" "paid")
   (create-order customer_id "closed" "shipped" "paid")
   (create-order customer_id "closed" "shipped" "paid")
   (create-order customer_id "closed" "pending" "paid")])


(defn drop-items-table []
  (jdbc/execute! db
                 ["DROP TABLE IF EXISTS items"]))

(defn create-items-table []
  (jdbc/execute! db
                 ["CREATE TABLE IF NOT EXISTS items (
                    sku VARCHAR(255) NOT NULL,
                    order_id INTEGER NOT NULL,
                    name VARCHAR(255) NOT NULL,
                    price INTEGER NOT NULL,
                    quantity INTEGER NOT NULL,
                    CONSTRAINT fk_order
                      FOREIGN KEY(order_id)
                         REFERENCES orders(id)
                  )"]))

(defn add-line-item [order_id sku name price quantity]
  (jdbc/execute! db
                 ["INSERT INTO items (sku, order_id, name, price, quantity) VALUES (?, ?, ?, ?, ?)"
                  sku order_id name price quantity]))

(defn add-dummy-items [order_id]
  (case order_id
    1
    (do
      (add-line-item order_id "sku-1" "item-1" 100 1)
      (add-line-item order_id "sku-2" "item-2" 200 2)
      (add-line-item order_id "sku-3" "item-3" 300 3)
      (add-line-item order_id "sku-4" "item-4" 400 4))
    2
    (do
      (add-line-item order_id "sku-1" "item-1" 120 1)
      (add-line-item order_id "sku-2" "item-2" 220 2)
      (add-line-item order_id "sku-3" "item-3" 320 3))
    3
    (do
      (add-line-item order_id "sku-1" "item-1" 130 1)
      (add-line-item order_id "sku-3" "item-3" 330 3)
      (add-line-item order_id "sku-4" "item-4" 430 4))
    4
    (do
      (add-line-item order_id "sku-2" "item-2" 240 2)
      (add-line-item order_id "sku-3" "item-3" 340 3)
      (add-line-item order_id "sku-4" "item-4" 440 4))))


(defn seed-db! []
  (drop-items-table)
  (drop-orders-table)
  (drop-customer-table)
  (create-customer-table)
  (create-orders-table)
  (create-items-table)
  (->>
   (create-customer "Lars Monsen" "fjell@klatrer.no" +4781549300 "Fjellklatrerveien 2" "Sandefjord" 3216 "Norway")
   (add-dummy-orders)
   (mapv add-dummy-items)))

(comment
  (seed-db!))
