(ns darkexchange.controller.main.main-frame
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.main.home-tab :as home-tab]
            [darkexchange.controller.main.main-menu-bar :as main-menu-bar]
            [darkexchange.controller.main.peer-tab :as peer-tab]
            [darkexchange.controller.main.search.search-tab :as search-tab]
            [darkexchange.controller.utils :as controller-utils]
            [darkexchange.view.main.main-frame :as view-main-frame]))

(defn show []
  (controller-utils/show
    (search-tab/init (home-tab/init (peer-tab/init (main-menu-bar/init (view-main-frame/create)))))))