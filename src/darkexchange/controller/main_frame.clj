(ns darkexchange.controller.main-frame
  (:require [darkexchange.controller.main-menu-bar :as controller-main-menu-bar]
            [darkexchange.controller.peer-tab :as peer-tab]
            [darkexchange.view.main.main-frame :as view-main-frame]
            [darkexchange.view.util :as view-util]
            [clojure.contrib.logging :as logging]))

(defn show []
  (let [main-frame-map (view-main-frame/show)]
    (controller-main-menu-bar/attach-main-menu-actions main-frame-map)
    (peer-tab/load-destination (view-util/get-widget main-frame-map))))