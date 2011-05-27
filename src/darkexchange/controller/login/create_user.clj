(ns darkexchange.controller.login.create-user
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as actions-utils]
            [darkexchange.model.user :as user-model]
            [darkexchange.view.login.create-user :as create-user-view]
            [seesaw.core :as seesaw-core]))

(defn attach-cancel-action [create-user-frame]
  (actions-utils/attach-window-close-listener create-user-frame "#cancel-button"))

(defn find-user-name-text [create-user-frame]
  (actions-utils/find-component create-user-frame "#user-name-text"))

(defn find-password-field1-text [create-user-frame]
  (actions-utils/find-component create-user-frame "#password-field1"))

(defn find-password-field2-text [create-user-frame]
  (actions-utils/find-component create-user-frame "#password-field2"))

(defn reset-password [password-field]
  (.setText password-field ""))

(defn create-user-error [create-user-frame message]
  (seesaw-core/alert message)
  (reset-password (find-password-field1-text create-user-frame))
  (reset-password (find-password-field2-text create-user-frame)))

(defn user-name [create-user-frame]
  (if-let [user-name (user-model/validate-user-name (seesaw-core/text (find-user-name-text create-user-frame)))]
    user-name
    (create-user-error create-user-frame "The user name you entered is invalid. Either you didn't enter a user name, or the one you entered is already taken.")))

(defn password1 [create-user-frame]
  (.getPassword (find-password-field1-text create-user-frame)))

(defn password2 [create-user-frame]
  (.getPassword (find-password-field2-text create-user-frame)))

(defn password [create-user-frame]
  (if-let [password (user-model/validate-passwords (password1 create-user-frame) (password2 create-user-frame))]
    password
    (create-user-error create-user-frame "The passwords you entered are not the same. Please enter them again.")))

(defn create-user [create-user-frame]
  (when-let [user-name (user-name create-user-frame)]
    (when-let [password (password create-user-frame)]
      (try
        (user-model/insert { :name user-name :password password })
        (actions-utils/close-window create-user-frame)
        (catch Throwable t
          (logging/error "" t))))))

(defn attach-register-action [create-user-frame]
  (actions-utils/attach-listener create-user-frame "#register-button" (fn [_] (create-user create-user-frame))))

(defn attach [create-user-frame]
  (attach-cancel-action create-user-frame)
  (attach-register-action create-user-frame))

(defn show []
  (let [create-user-frame (create-user-view/show)]
    (attach create-user-frame)))