(ns darkexchange.controller.add-destination.add-destination
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as actions-utils]
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
    :action actions-utils/close-window))

(defn add-action [add-destination-frame call-back]
  (let [destination (.getText (find-destination-text add-destination-frame))]
    (peer-model/add-destination destination)
    (peer-model/notify-peer-if-necessary destination))
  (peer-model/download-peers)
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