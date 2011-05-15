(ns darkexchange.view.main.main-frame
  (:require [darkexchange.model.terms :as terms]
            [darkexchange.view.main.main-menu-bar :as main-menu-bar]
            [darkexchange.view.main.tabbed-pane :as tabbed-pane]
            [darkexchange.view.util :as view-util]
            [seesaw.core :as seesaw-core]))



(defn show []
  (let [menu-bar-map (main-menu-bar/create)]
    (view-util/make-widget
      (seesaw-core/frame
        :title (terms/dark-exchange)
        :menubar (view-util/get-widget menu-bar-map)
        :content (tabbed-pane/create)
        :on-close :exit)
      :menubar menu-bar-map)))