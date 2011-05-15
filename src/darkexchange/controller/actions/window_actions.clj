(ns darkexchange.controller.actions.window-actions
  (:require [seesaw.core :as seesaw-core]))

(defn close-window [e]
  (let [frame (seesaw-core/to-frame e)]
    (.hide frame)
    (.dispose frame)))