(ns darkexchange.controller.main-frame
  (:require [darkexchange.controller.main-menu-bar :as main-menu-bar]
            [darkexchange.controller.peer-tab :as peer-tab]
            [darkexchange.view.main.main-frame :as view-main-frame]
            [clojure.contrib.logging :as logging]))

(defn show []
  (let [main-frame (view-main-frame/show)]
    (main-menu-bar/attach main-frame)
    (peer-tab/attach main-frame)))