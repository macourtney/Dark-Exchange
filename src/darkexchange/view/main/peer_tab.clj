(ns darkexchange.view.main.peer-tab
  (:require [darkexchange.model.terms :as terms]
            [seesaw.core :as seesaw-core]))

(def tab-name (terms/peer))

(defn create-destination-text []
  (seesaw-core/scrollable
    (seesaw-core/text
      :id :destination-text
      :multi-line? true
      :editable? false
      :preferred-size [200 :by 60])))

(defn create-destination-panel []
  (seesaw-core/horizontal-panel :id :north-panel :items [(terms/destination-address) [:fill-h 3] (create-destination-text)]))

(defn create []
  (let [peer-tab-panel (seesaw-core/border-panel :border 5 :north (create-destination-panel) :center "Peer Tab")]
    (.setName peer-tab-panel "peer-tab-panel")
    peer-tab-panel))