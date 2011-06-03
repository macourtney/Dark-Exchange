(ns darkexchange.view.main.search.search-tab
  (:require [darkexchange.model.terms :as terms]
            [darkexchange.view.main.search.has-panel :as has-panel]
            [darkexchange.view.main.search.wants-panel :as wants-panel]
            [seesaw.core :as seesaw-core]))

(def tab-name (terms/search))

(def table-columns [ { :key :name :text (terms/name) }
                     { :key :wants :text (terms/wants) }
                     { :key :to_receive_by :text (terms/to-receive-by) }
                     { :key :has :text (terms/has) }
                     { :key :to_send_by :text (terms/to-send-by) } ])

(defn create-criteria-panel []
  (seesaw-core/horizontal-panel
    :border 5
    :items [(seesaw-core/vertical-panel :items [(has-panel/create) [:fill-v 5] (wants-panel/create)])
            [:fill-h 5]
            (seesaw-core/button :id :search-button :text (terms/search))]))

(defn list-buttons []
  (seesaw-core/horizontal-panel :items 
    [ (seesaw-core/button :id :view-offer-button :text (terms/view)) ]))

(defn list-header-panel []
  (seesaw-core/border-panel
    :west (terms/offers)
    :east (list-buttons)))

(defn list-table []
  (seesaw-core/scrollable
    (seesaw-core/table :id :search-offer-table :preferred-size [600 :by 300]
      :model [ :columns table-columns ])))

(defn create-list-panel []
  (seesaw-core/border-panel
    :vgap 3
    :north (list-header-panel)
    :center (list-table)))

(defn create []
  (seesaw-core/border-panel
    :border 5
    :vgap 5
    :north (create-criteria-panel)
    :center (create-list-panel)))