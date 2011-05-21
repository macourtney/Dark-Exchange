(ns darkexchange.model.property
  (:require [clj-record.boot :as clj-record-boot]
            [clojure.contrib.logging :as logging])
  (:use darkexchange.model.base))

(clj-record.core/init-model)

(def peers-downloaded?-property "peers-downloaded?")

(def properties (atom {}))

(defn load-properties []
  (swap! properties merge (reduce #(assoc %1 (:name %2) (:value %2)) {} (find-records [true]))))

(defn get-property [property-name]
  (get @properties property-name))

(defn set-db-property [property-name property-value]
  (if-let [db-property (find-record { :name property-name })]
    (update (assoc db-property :value property-value))
    (insert { :name property-name :value property-value })))

(defn set-property [property-name property-value]
  (let [property-value-str (when property-value (str property-value))]
    (swap! properties assoc property-name property-value-str)

    ;Update thet database in the background.
    (.start (Thread. #(set-db-property property-name property-value-str)))))

(defn peers-downloaded? []
  (get-property peers-downloaded?-property))

(defn test-peers-downloaded? []
  (let [downloaded? (peers-downloaded?)]
    (when-not downloaded?
      (set-property peers-downloaded?-property true))
    downloaded?))

(defn reset-peers-downloaded? []
  (set-property peers-downloaded?-property nil))