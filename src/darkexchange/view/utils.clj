(ns darkexchange.view.utils)

(defn center-window-on [parent window]
  (.setLocationRelativeTo window parent)
  window)

(defn center-window [window]
  (center-window-on nil window))