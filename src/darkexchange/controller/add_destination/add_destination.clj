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

(defn add-destination-cleanup [add-destination-frame call-back]
  (seesaw-core/invoke-later
    (call-back)
    (actions-utils/close-window add-destination-frame)))

(defn add-destination [add-destination-frame call-back destination]
  (future
    (peer-model/add-destination-if-missing destination)
    (peer-model/notify-peer-if-necessary destination)
    (peer-model/download-peers)
    (add-destination-cleanup add-destination-frame call-back)))

(defn add-action [add-destination-frame e call-back]
  (controller-utils/disable-widget add-destination-frame)
  (add-destination add-destination-frame call-back (.getText (find-destination-text add-destination-frame))))

(defn attach-add-action [add-destination-frame call-back]
  (actions-utils/attach-frame-listener add-destination-frame "#add-button"
    #(add-action %1 %2 call-back)))

(defn attach [add-destination-frame call-back]
  (attach-add-action (attach-cancel-action add-destination-frame) call-back))

(defn show [main-frame call-back]
  (controller-utils/show (attach (add-destination-view/create main-frame) call-back)))