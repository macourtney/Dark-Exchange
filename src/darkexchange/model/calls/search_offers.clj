(ns darkexchange.model.calls.search-offers
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.peer :as peer-model]))

(defn add-info-to-offer [response-map offer]
  (merge offer
    { :name (:name (:user (:from response-map)))
      :public-key (:public-key (:user (:from response-map)))
      :public-key-algorithm (:public-key-algorithm (:user (:from response-map))) }))

(defn search-offers-call-back [response-map call-back]
  (if-let [data (:data response-map)]
    (call-back (map #(add-info-to-offer response-map %) data))
    (call-back nil)))

(defn call [i-have-currency i-have-payment-type i-want-currency i-want-payment-type call-back]
  (peer-model/send-messages action-keys/search-offers-action-key
    { :i-have-currency (:code i-have-currency)
      :i-have-payment-type (:code i-have-payment-type)
      :i-want-currency (:code i-want-currency)
      :i-want-payment-type (:code i-want-payment-type) }
    #(search-offers-call-back % call-back)))