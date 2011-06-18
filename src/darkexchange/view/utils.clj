(ns darkexchange.view.utils
  (:require [seesaw.core :as seesaw-core]))

(defn center-window-on [parent window]
  (seesaw-core/pack! window)
  (.setLocationRelativeTo window parent)
  window)

(defn center-window [window]
  (center-window-on nil window))