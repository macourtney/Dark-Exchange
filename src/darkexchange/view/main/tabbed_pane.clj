(ns darkexchange.view.main.tabbed-pane
  (:require [darkexchange.view.main.home-tab :as home-tab]
            [darkexchange.view.main.identity-tab :as identity-tab]
            [darkexchange.view.main.peer-tab :as peer-tab]
            [darkexchange.view.main.search.search-tab :as search-tab]
            [seesaw.core :as seesaw-core]))

(defn create-tabs []
  [ { :title home-tab/tab-name :content (home-tab/create) }
    { :title search-tab/tab-name :content (search-tab/create) }
    { :title identity-tab/tab-name :content (identity-tab/create) }
    { :title peer-tab/tab-name :content (peer-tab/create) } ])

(defn create []
  (seesaw-core/tabbed-panel :id :main-tabbed-pane
    :tabs (create-tabs)))