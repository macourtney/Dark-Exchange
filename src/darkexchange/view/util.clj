(ns darkexchange.view.util
  (:require [clojure.contrib.logging :as logging]))

(def widget-key :widget)

(defn set-widget
  ([component] (set-widget {} component))
  ([widget-map component]
    (assoc widget-map widget-key component)))

(defn get-widget [widget-map]
  (widget-key widget-map))

(defn merge-widget [widget-map widget-generator]
  (merge widget-map (widget-generator widget-map)))

(defn save [widget-map & vars]
  (reduce #(assoc %1 (first %2) (second %2)) widget-map (partition 2 vars)))

(defn make-widget [component & vars]
  (apply save (set-widget component) vars))