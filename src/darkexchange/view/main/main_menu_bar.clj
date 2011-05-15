(ns darkexchange.view.main.main-menu-bar
  (:require [darkexchange.model.terms :as terms]
            [darkexchange.view.util :as view-util]
            [seesaw.core :as seesaw-core]))

(defn exit []
  (view-util/make-widget (seesaw-core/menu-item :id :exit-menu-item :text (terms/exit))))

(defn file []
  (let [exit-menu (exit)]
    (view-util/make-widget (seesaw-core/menu :text (terms/file) :items [(view-util/get-widget exit-menu)])
      :exit exit-menu)))

(defn create []
  (let [file-menu (file)]
    (view-util/make-widget (seesaw-core/menubar :items [(view-util/get-widget file-menu)])
      :file file-menu)))