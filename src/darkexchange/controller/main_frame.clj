(ns darkexchange.controller.main-frame
  (:require [darkexchange.controller.main-menu-bar :as controller-main-menu-bar]
            [darkexchange.controller.peer-tab :as peer-tab]
            [darkexchange.view.main.main-frame :as view-main-frame]
            [darkexchange.view.util :as view-util]
            [clojure.contrib.logging :as logging]))

(defn show []
  (let [main-frame (view-main-frame/show)]
    (controller-main-menu-bar/attach-main-menu-actions main-frame)
    (peer-tab/load-destination main-frame)))