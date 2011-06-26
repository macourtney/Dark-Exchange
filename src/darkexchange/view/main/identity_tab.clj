(ns darkexchange.view.main.identity-tab
  (:require [darkexchange.model.terms :as terms]
            [seesaw.core :as seesaw-core]))

(def tab-name (terms/identity))

(def identity-table-columns [ { :key :id :text (terms/id) }
                              { :key :name :text (terms/name) }
                              { :key :public_key :text (terms/public-key) }
                              { :key :public_key_algorithm :text (terms/algorithm) }
                              { :key :destination :text (terms/destination) }])

(defn create-title-panel []
  (seesaw-core/border-panel
    :west (terms/identities)))

(defn create-identity-table []
  (seesaw-core/scrollable
    (seesaw-core/table :id :identity-table :preferred-size [600 :by 300]
      :model [ :columns identity-table-columns ])))

(defn create []
  (seesaw-core/border-panel
    :border 5
    :vgap 5
    :north (create-title-panel)
    :center (create-identity-table)))