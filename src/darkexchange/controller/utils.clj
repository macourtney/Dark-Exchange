(ns darkexchange.controller.utils
  (:require [clojure.contrib.logging :as logging]
            [seesaw.core :as seesaw-core])
  (:import [java.awt.event ItemListener]))

(defn find-component [parent-component id]
  (seesaw-core/select parent-component [id]))

(defn show [frame]
  (seesaw-core/show! frame))

(defn create-item-listener [item-listener-fn]
  (reify ItemListener
    (itemStateChanged [this e]
      (item-listener-fn e))))

(defn attach-item-listener [component item-listener-fn]
  (.addItemListener component (create-item-listener item-listener-fn))
  component)