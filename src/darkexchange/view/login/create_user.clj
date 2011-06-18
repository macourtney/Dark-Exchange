(ns darkexchange.view.login.create-user
  (:require [darkexchange.model.terms :as terms]
            [darkexchange.view.utils :as view-utils]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing JPasswordField]))

(defn create-user-name-panel []
  (seesaw-core/horizontal-panel :items
    [ (terms/user-name)
      [:fill-h 3]
      (seesaw-core/text :id :user-name-text :preferred-size [150 :by 25])]))

(defn password-field [& args]
  (let [password-field (new JPasswordField)]
    (when (and args (> (count args) 0))
      (apply seesaw-core/config! password-field args))
    password-field))

(defn create-password-panel1 []
  (seesaw-core/horizontal-panel :items
    [ (terms/enter-password)
      [:fill-h 3]
      (password-field :id :password-field1 :preferred-size [120 :by 25])]))

(defn create-password-panel2 []
  (seesaw-core/horizontal-panel :items
    [ (terms/reenter-password)
      [:fill-h 3]
      (password-field :id :password-field2 :preferred-size [120 :by 25])]))

(defn create-button-panel []
  (seesaw-core/border-panel :east
    (seesaw-core/horizontal-panel :items
      [(seesaw-core/button :id :register-button :text (terms/register))
       [:fill-h 3]
       (seesaw-core/button :id :cancel-button :text (terms/cancel))])))

(defn create-content []
  (seesaw-core/vertical-panel
    :border 5
    :items [(create-user-name-panel) [:fill-v 3] (create-password-panel1)[:fill-v 3] (create-password-panel2)
            [:fill-v 5] (create-button-panel)]))

(defn create [login-frame]
  (view-utils/center-window-on login-frame
    (seesaw-core/frame
      :title (terms/dark-exchange-login)
      :content (create-content)
      :visible? false)))