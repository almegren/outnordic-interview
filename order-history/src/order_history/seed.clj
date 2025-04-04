(ns order-history.seed
  (:require [next.jdbc :as jdbc]))


(def db {:dbtype "postgresql"
         :dbname "order-history"
         :host "localhost"
         :port 5432
         :user "test"
         :password "test1234"})

(def db-spec (jdbc/get-datasource db))

(defn create-customer-table []
  (jdbc/execute! db-spec
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
   (jdbc/execute-one! db-spec
                 ["INSERT INTO customers (name, email, phone, address, city, postalcode, country) VALUES (?, ?, ?, ?, ?, ?, ?)
                   RETURNING id"
                  name email phone address city postalcode country])
   :customers/id))

(defn create-orders-table []
  (jdbc/execute! db-spec
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
   (jdbc/execute-one! db-spec
                      ["INSERT INTO orders (customer_id, status, shipment_state, payment_state) VALUES (?, ?, ?, ?)
                        RETURNING id"
                       customer_id status shipment_state payment_state])
   :orders/id))

(defn add-dummy-orders [customer_id]
  [(create-order customer_id "open" "shipped" "paid")
   (create-order customer_id "closed" "shipped" "paid")
   (create-order customer_id "closed" "shipped" "paid")
   (create-order customer_id "closed" "pending" "paid")
   ])

(defn create-items-table []
  (jdbc/execute! db-spec
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
  (jdbc/execute! db-spec
                 ["INSERT INTO items (sku, order_id, name, price, quantity) VALUES (?, ?, ?, ?, ?)"
                  sku order_id name price quantity]))

(defn add-dummy-items [order_id]
  (add-line-item order_id "sku-1" "item-1" 100 1)
  (add-line-item order_id "sku-2" "item-2" 200 2)
  (add-line-item order_id "sku-3" "item-3" 300 3)
  (add-line-item order_id "sku-4" "item-4" 400 4))

(defn seed-db! []
  (create-customer-table)
  (create-orders-table)
  (create-items-table)
  (->>
   (create-customer "Lars Monsen" "fjell@klatrer.no" +4781549300 "Fjellklatrerveien 2" "Sandefjord" 3216 "Norway")
   (add-dummy-orders) 
   (mapv add-dummy-items)))
