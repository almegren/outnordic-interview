(ns order-history.app
  (:gen-class)
  (:require
   [muuntaja.core :as m]
   [reitit.ring :as ring]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [ring.adapter.jetty :as jetty :refer [run-jetty]]
   [order-history.api :as api]))


(def routes
  [["/customer/:customer-id/order/"
    {:get {:summary "Get all orders for a customer"
           :responses {200 {:body any?}}
           :handler api/customer-order-handler}}]
   ["/customer/:customer-id/item/:item-sku"
    {:get {:summary "Get the order history for an item"
           :responses {200 {:body any?}}
           :handler api/customer-item-handler}}]])

(def app
  (ring/ring-handler
    (ring/router
      routes
      {:data {:muuntaja m/instance
              :middleware [parameters/parameters-middleware
                           muuntaja/format-negotiate-middleware
                           muuntaja/format-response-middleware]}})
    (ring/create-default-handler)))

(defonce server (atom nil))

(defn start-server []
  (reset! server
          (run-jetty #'app {:port 3000 :join? false}))
  :started)

(defn stop-server []
  (when @server
    (.stop @server)
    (reset! server nil)
    :stopped))

(defn restart-server []
  (stop-server)
  (start-server))

(comment
  (restart-server))

