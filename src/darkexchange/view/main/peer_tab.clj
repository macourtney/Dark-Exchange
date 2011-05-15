(ns darkexchange.view.main.peer-tab
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.terms :as terms]
            [darkexchange.view.util :as view-util]
            [seesaw.core :as seesaw-core]))

(def tab-name (terms/peer))

(defn create-destination-text-area []
  (let [text-area (seesaw-core/text
                    :id :destination-text
                    :multi-line? true
                    :editable? false
                    :preferred-size [400 :by 60])]
    (.setLineWrap text-area true)
    text-area))

(defn create-destination-text []
  (seesaw-core/scrollable (create-destination-text-area)))

(defn create-destination-panel []
  (seesaw-core/vertical-panel 
    :id :north-panel 
    :items [(terms/destination-address) [:fill-v 3] (create-destination-text)]))

(defn create []
  (seesaw-core/border-panel
      :border 5
      :vgap 5
      :north (create-destination-panel)
      :center "Peer Tab"))