(ns darkexchange.controller.login.login
  (:require [darkexchange.controller.actions.window-actions :as window-actions]
            [darkexchange.model.user :as user-model]
            [darkexchange.view.login.login :as login-view]
            [seesaw.core :as seesaw-core]))

(defn find-user-name-combobox [login-frame]
  (seesaw-core/select login-frame ["#user-name-combobox"]))

(defn reload-user-name-combobox [login-frame]
  (seesaw-core/config! (find-user-name-combobox login-frame) :model (user-model/all-user-names)))

(defn load-data [login-frame]
  (reload-user-name-combobox login-frame))

(defn find-cancel-button [login-frame]
  (seesaw-core/select login-frame ["#cancel-button"]))

(defn attach-cancel-action [login-frame]
  (seesaw-core/listen (find-cancel-button login-frame)
    :action window-actions/close-window-and-exit))

(defn attach [login-frame]
  (attach-cancel-action login-frame))

(defn show []
  (let [login-frame (login-view/show)]
    (load-data login-frame)
    (attach login-frame)))