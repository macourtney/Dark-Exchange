(ns darkexchange.view.main.home-tab
  (:require [darkexchange.model.terms :as terms]
            [darkexchange.view.util :as view-util]
            [seesaw.core :as seesaw-core]))

(def tab-name (terms/home))

(defn create []
  (seesaw-core/border-panel :center "Home Tab"))