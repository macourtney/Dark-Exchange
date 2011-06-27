(ns darkexchange.model.interceptors.signature-interceptor
  (:require [clojure.contrib.json :as json]
            [clojure.contrib.logging :as logging]
            [darkexchange.interchange-map-util :as interchange-map-util]
            [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.identity :as identity-model]
            [darkexchange.model.interceptors.header-reply-interceptor :as header-reply-interceptor]
            [darkexchange.model.user :as user-model]))

(def unsigned-actions #{ (name action-keys/notify-action-key) })

(defn sign [response-map]
  (let [str-data (json/json-str (dissoc response-map :destination))]
    { :from (header-reply-interceptor/response-map-from)
      :action (:action response-map)
      :destination (:destination response-map)
      :data { :signature (user-model/sign str-data) :data str-data } }))

(defn unsigned-action? [request-map]
  (when-let [action (:action request-map)]
    (contains? unsigned-actions action)))

(defn verify [request-map]
  (or
    (unsigned-action? request-map)
    (let [other-identity { :public_key (interchange-map-util/from-public-key request-map)
                           :public_key_algorithm (interchange-map-util/from-public-key-algorithm request-map) }]
      (let [data (:data request-map)]
        (identity-model/verify-signature other-identity (:data data) (:signature data))))))

(defn invalid-signature [interchange-map]
  { :from (:from interchange-map) :type :invalid-signature })

(defn parse-data [interchange-map]
  (assoc (json/read-json (:data (:data interchange-map))) :from (:from interchange-map)))

(defn server-interceptor [action request-map]
  (try
    (if (verify request-map)
      (sign (action (parse-data request-map)))
      (invalid-signature request-map))
    (catch Throwable t
      (logging/error "an error occured while signing or verifying a signature on the server side." t))))

(defn client-interceptor [action request-map]
  (try
    (let [response-map (action (sign request-map))]
      (if (verify response-map)
        (parse-data response-map)
        (invalid-signature response-map)))
    (catch Throwable t
      (logging/error "an error occured while signing or verifying a signature on the client side." t))))