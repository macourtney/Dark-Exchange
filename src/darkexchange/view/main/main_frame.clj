(ns darkexchange.view.main.main-frame
  (:require [seesaw.core :as seesaw-core]
            [darkexchange.model.terms :as terms]
            [darkexchange.view.main.main-menu-bar :as main-menu-bar]))

(defn show []
  (seesaw-core/frame :title (terms/dark-exchange) :menubar (main-menu-bar/create) :content "Hello World" :on-close :exit))