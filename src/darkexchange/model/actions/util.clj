(ns darkexchange.model.actions.util
  (:require [darkexchange.model.identity :as identity-model]))

(defn from-map [request-map]
  (:from request-map))

(defn from-destination [request-map]
  (:destination (from-map request-map)))

(defn from-user-map [request-map]
  (:user (from-map request-map)))

(defn from-user-name [request-map]
  (:name (from-user-map request-map)))

(defn from-public-key [request-map]
  (:public-key (from-user-map request-map)))

(defn from-identity [request-map]
  (identity-model/find-identity (from-user-name request-map) (from-public-key request-map)))