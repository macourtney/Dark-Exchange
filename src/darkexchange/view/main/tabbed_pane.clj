(ns darkexchange.view.main.tabbed-pane
  (:require [darkexchange.view.main.home-tab :as home-tab]
            [darkexchange.view.main.peer-tab :as peer-tab]
            [darkexchange.view.util :as view-util]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing JTabbedPane]))

(defn add-tabs [tabbed-pane]
  (.addTab tabbed-pane home-tab/tab-name (home-tab/create))
  (.addTab tabbed-pane peer-tab/tab-name (peer-tab/create)))

(defn create []
  (let [tabbed-pane (new JTabbedPane)]
    (seesaw-core/config! tabbed-pane :id :main-tabbed-pane)
    (add-tabs tabbed-pane)
    tabbed-pane))