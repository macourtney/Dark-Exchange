(ns darkexchange.controller.widgets.utils
  (:require [seesaw.core :as seesaw-core]))

(defn load-combobox [combobox data]
  (seesaw-core/config! combobox :model data))

(defn find-widget [parent-component widget-selector]
  (seesaw-core/select parent-component widget-selector))