(ns darkexchange.controller.add-destination.add-destination
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.window-actions :as window-actions]
            [darkexchange.model.peer :as peer-model]
            [darkexchange.model.terms :as terms]
            [darkexchange.view.add-destination.add-destination :as add-destination-view]
            [seesaw.core :as seesaw-core]))

(defn find-cancel-button [add-destination-frame]
  (seesaw-core/select add-destination-frame ["#cancel-button"]))

(defn find-add-button [add-destination-frame]
  (seesaw-core/select add-destination-frame ["#add-button"]))

(defn find-destination-text [add-destination-frame]
  (seesaw-core/select add-destination-frame ["#destination-text"]))

(defn attach-cancel-action [add-destination-frame]
  (seesaw-core/listen (find-cancel-button add-destination-frame)
    :action window-actions/close-window))

(defn add-action [add-destination-frame call-back]
  (peer-model/add-destination (.getText (find-destination-text add-destination-frame)))
  (call-back)
  (.hide add-destination-frame)
  (.dispose add-destination-frame))

(defn attach-add-action [add-destination-frame call-back]
  (seesaw-core/listen (find-add-button add-destination-frame)
    :action (fn [e] (add-action add-destination-frame call-back))))

(defn attach [add-destination-frame call-back]
  (attach-add-action add-destination-frame call-back)
  (attach-cancel-action add-destination-frame))

(defn show [call-back]
  (attach (add-destination-view/show) call-back))