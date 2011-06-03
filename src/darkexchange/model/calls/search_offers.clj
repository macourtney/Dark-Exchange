(ns darkexchange.model.calls.search-offers
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.peer :as peer-model]))



(defn search-offers-call-back [request-map call-back]
  (if-let [data (:data request-map)]
    (call-back (map #(assoc % :name (:name (:user (:from request-map)))) data))
    (call-back nil)))

(defn call [i-have-currency i-have-payment-type i-want-currency i-want-payment-type call-back]
  (peer-model/send-messages action-keys/search-offers-action-key
    { :i-have-currency (:code i-have-currency)
      :i-have-payment-type (:code i-have-payment-type)
      :i-want-currency (:code i-want-currency)
      :i-want-payment-type (:code i-want-payment-type) }
    #(search-offers-call-back % call-back)))