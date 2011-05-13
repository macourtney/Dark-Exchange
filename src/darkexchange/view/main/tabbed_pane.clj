(ns darkexchange.view.main.tabbed-pane
  (:require [darkexchange.view.main.home-tab :as home-tab]
            [darkexchange.view.main.peer-tab :as peer-tab]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing JTabbedPane]))

(defn create []
  (let [tabbed-pane(new JTabbedPane)]
    (.addTab tabbed-pane home-tab/tab-name (home-tab/create))
    (.addTab tabbed-pane peer-tab/tab-name (peer-tab/create))
    (seesaw-core/apply-default-opts tabbed-pane { :id :main-tabbed-pane })
    tabbed-pane))