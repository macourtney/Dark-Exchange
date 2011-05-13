(ns darkexchange.controller.main-frame
  (:require [darkexchange.controller.main-menu-bar :as controller-main-menu-bar]
            [darkexchange.controller.peer-tab :as peer-tab]
            [darkexchange.view.main.main-frame :as view-main-frame]
            [clojure.contrib.logging :as logging]))

(defn show []
  (let [frame (view-main-frame/show)]
    (controller-main-menu-bar/attach-main-menu-actions frame)
    (peer-tab/load-destination frame)))