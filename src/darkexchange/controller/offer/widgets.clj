(ns darkexchange.controller.offer.widgets
  (:require [seesaw.core :as seesaw-core]))

(defn load-combobox [combobox data]
  (seesaw-core/config! combobox :model data))