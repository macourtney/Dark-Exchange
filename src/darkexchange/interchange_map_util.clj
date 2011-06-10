(ns darkexchange.interchange-map-util
  (:require [darkexchange.model.identity :as identity-model]))

(defn from-map [interchange-map]
  (:from interchange-map))

(defn from-destination [interchange-map]
  (:destination (from-map interchange-map)))

(defn from-user-map [interchange-map]
  (:user (from-map interchange-map)))

(defn from-user-name [interchange-map]
  (:name (from-user-map interchange-map)))

(defn from-public-key [interchange-map]
  (:public-key (from-user-map interchange-map)))

(defn from-public-key-algorithm [interchange-map]
  (:public-key-algorithm (from-user-map interchange-map)))

(defn from-identity [interchange-map]
  (identity-model/find-identity (from-user-name interchange-map) (from-public-key interchange-map)
    (from-public-key-algorithm interchange-map)))