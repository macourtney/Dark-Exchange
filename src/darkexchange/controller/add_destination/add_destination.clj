(ns darkexchange.controller.add-destination.add-destination
  (:require [darkexchange.controller.actions.window-actions :as window-actions]
            [darkexchange.model.terms :as terms]
            [darkexchange.view.add-destination.add-destination :as add-destination-view]
            [seesaw.core :as seesaw-core]))

(defn find-cancel-button [add-destination-frame]
  (seesaw-core/select add-destination-frame ["#cancel-button"]))

(defn attach-cancel-action [add-destination-frame]
  (seesaw-core/listen (find-cancel-button add-destination-frame)
    :action window-actions/close-window))

(defn attach [add-destination-frame]
  (seesaw-core/select add-destination-frame ["#add-button"])
  (attach-cancel-action add-destination-frame))

(defn show []
  (attach (add-destination-view/show)))