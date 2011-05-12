(ns darkexchange.view.main.main-menu-bar
  (:require [seesaw.core :as seesaw-core]
            [darkexchange.model.terms :as terms]))

(defn exit []
  (seesaw-core/menu-item :id :exit-menu-item :text (terms/exit)))

(defn file []
  (seesaw-core/menu :text (terms/file) :items 
    [(exit)]))

(defn create []
  (seesaw-core/menubar :items
    [(file)]))