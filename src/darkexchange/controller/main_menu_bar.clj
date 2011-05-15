(ns darkexchange.controller.main-menu-bar
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.view.util :as view-util]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing JMenuItem JMenu]))

(defn attach-exit-action [menu-map]
  (let [menu (view-util/get-widget menu-map)]
    (when (= (seesaw-core/id-for menu) "exit-menu-item")
      (seesaw-core/listen menu :action
        (fn [e]
          (let [frame (seesaw-core/to-frame e)]
            (.hide frame)
            (.dispose frame)))))))

(defn attach-actions-to-menus [menu-bar-map]
  (attach-exit-action (:exit (:file menu-bar-map))))

(defn attach-main-menu-actions [main-frame-map]
  (logging/debug (str "(seesaw-core/select main-frame [:#exit-menu-item]): " (seesaw-core/select (view-util/get-widget main-frame-map) [:#exit-menu-item])))
  (attach-actions-to-menus (:menubar main-frame-map)))