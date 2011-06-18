(ns darkexchange.view.main.main-frame
  (:require [darkexchange.model.terms :as terms]
            [darkexchange.view.main.main-menu-bar :as main-menu-bar]
            [darkexchange.view.main.tabbed-pane :as tabbed-pane]
            [darkexchange.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(defn create []
  (view-utils/center-window
    (seesaw-core/frame
      :title (terms/dark-exchange)
      :menubar (main-menu-bar/create)
      :content (tabbed-pane/create)
      :on-close :exit
      :visible? false)))