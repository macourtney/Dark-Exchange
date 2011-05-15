(ns darkexchange.view.main.tabbed-pane
  (:require [darkexchange.view.main.home-tab :as home-tab]
            [darkexchange.view.main.peer-tab :as peer-tab]
            [darkexchange.view.util :as view-util]
            [seesaw.core :as seesaw-core]))

(defn create-tabs []
  [ { :title home-tab/tab-name :content (home-tab/create) }
    { :title peer-tab/tab-name :content (peer-tab/create) } ])

(defn create []
  (seesaw-core/tabbed-panel :id :main-tabbed-pane
    :tabs (create-tabs)))