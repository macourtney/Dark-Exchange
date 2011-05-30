(ns darkexchange.controller.main.main-frame
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.main.home-tab :as home-tab]
            [darkexchange.controller.main.main-menu-bar :as main-menu-bar]
            [darkexchange.controller.main.peer-tab :as peer-tab]
            [darkexchange.controller.main.search.search-tab :as search-tab]
            [darkexchange.view.main.main-frame :as view-main-frame]))

(defn show []
  (let [main-frame (view-main-frame/show)]
    (main-menu-bar/attach main-frame)
    (peer-tab/attach main-frame)
    (home-tab/attach main-frame)
    (search-tab/attach main-frame)))