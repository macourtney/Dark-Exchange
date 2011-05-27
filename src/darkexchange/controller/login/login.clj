(ns darkexchange.controller.login.login
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as actions-utils]
            [darkexchange.controller.login.create-user :as create-user]
            [darkexchange.model.user :as user-model]
            [darkexchange.view.login.login :as login-view]
            [seesaw.core :as seesaw-core]))

(defn find-user-name-combobox [login-frame]
  (seesaw-core/select login-frame ["#user-name-combobox"]))

(defn reload-user-name-combobox [login-frame]
  (seesaw-core/config! (find-user-name-combobox login-frame) :model (user-model/all-user-names)))

(defn load-data [login-frame]
  (reload-user-name-combobox login-frame))

(defn attach-new-user-action [login-frame]
  (actions-utils/attach-listener login-frame "#new-user-button" (fn [_]  (create-user/show))))

(defn attach-cancel-action [login-frame]
  (actions-utils/attach-window-close-and-exit-listener login-frame "#cancel-button"))

(defn create-user-add-listener [login-frame]
  (fn [_]
    (reload-user-name-combobox login-frame)))

(defn attach-user-add-listener [login-frame]
  (user-model/add-user-add-listener (create-user-add-listener login-frame)))

(defn attach [login-frame]
  (attach-cancel-action login-frame)
  (attach-new-user-action login-frame)
  (attach-user-add-listener login-frame))

(defn show []
  (let [login-frame (login-view/show)]
    (load-data login-frame)
    (attach login-frame)))