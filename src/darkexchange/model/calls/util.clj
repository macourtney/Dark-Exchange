(ns darkexchange.model.calls.util)

(defn from-map [response-map]
  (:from response-map))

(defn from-destination [response-map]
  (:destination (from-map response-map)))

(defn from-user-map [response-map]
  (:user (from-map response-map)))

(defn from-user-name [response-map]
  (:name (from-user-map response-map)))

(defn from-public-key [response-map]
  (:public-key (from-user-map response-map)))

(defn from-public-key-algorithm [request-map]
  (:public-key-algorithm (from-user-map request-map)))