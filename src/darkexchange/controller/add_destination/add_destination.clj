(ns darkexchange.controller.add-destination.add-destination
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as actions-utils]
            [darkexchange.controller.utils :as controller-utils]
            [darkexchange.model.peer :as peer-model]
            [darkexchange.model.terms :as terms]
            [darkexchange.view.add-destination.add-destination :as add-destination-view]
            [seesaw.core :as seesaw-core]))

(defn find-destination-text [add-destination-frame]
  (seesaw-core/select add-destination-frame ["#destination-text"]))

(defn attach-cancel-action [add-destination-frame]
  (actions-utils/attach-window-close-listener add-destination-frame "#cancel-button"))

(defn add-action [add-destination-frame call-back]
  (let [destination (.getText (find-destination-text add-destination-frame))]
    (peer-model/add-destination destination)
    (peer-model/notify-peer-if-necessary destination))
  (peer-model/download-peers)
  (call-back)
  (.hide add-destination-frame)
  (.dispose add-destination-frame))

(defn attach-add-action [add-destination-frame call-back]
  (actions-utils/attach-listener add-destination-frame "#add-button"
    (fn [e] (add-action add-destination-frame call-back))))

(defn attach [add-destination-frame call-back]
  (attach-add-action (attach-cancel-action add-destination-frame) call-back))

(defn show [call-back]
  (controller-utils/show (attach (add-destination-view/create) call-back)))