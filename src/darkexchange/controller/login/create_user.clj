(ns darkexchange.controller.login.create-user
  (:require [darkexchange.controller.actions.utils :as actions-utils]
            [darkexchange.view.login.create-user :as create-user-view]))

(defn attach-cancel-action [create-user-frame]
  (actions-utils/attach-window-close-listener create-user-frame "#cancel-button"))

(defn attach [create-user-frame]
  (attach-cancel-action create-user-frame))

(defn show []
  (let [create-user-frame (create-user-view/show)]
    (attach create-user-frame)))