(ns darkexchange.view.main.identity-tab
  (:require [darkexchange.model.terms :as terms]
            [seesaw.core :as seesaw-core]))

(def tab-name (terms/identity))

(def identity-table-columns [ { :key :name :text (terms/name) }
                              { :key :public_key :text (terms/public-key) }
                              { :key :public_key_algorithm :text (terms/algorithm) }
                              { :key :destination :text (terms/destination) }
                              { :key :is_online :text (terms/is-online) }])

(defn create-show-only-online-identities-checkbox []
  (seesaw-core/checkbox :id :show-only-online-identites-checkbox :text (terms/show-only-online-identities)
    :selected? true))

(defn create-identities-table-title []
  (seesaw-core/horizontal-panel
    :items [ (terms/identities)
             [:fill-h 3]
             (create-show-only-online-identities-checkbox)]))

(defn create-identity-table-buttons []
  (seesaw-core/horizontal-panel :items 
    [ (seesaw-core/button :id :view-identity-button :text (terms/view) :enabled? false) ]))

(defn create-title-panel []
  (seesaw-core/border-panel
    :west (create-identities-table-title)
    :east (create-identity-table-buttons)))

(defn create-identity-table []
  (seesaw-core/scrollable
    (seesaw-core/table :id :identity-table :preferred-size [600 :by 300]
      :model [ :columns identity-table-columns ])))

(defn create-identity-table-panel []
  (seesaw-core/border-panel
    :border 5
    :vgap 5
    :north (create-title-panel)
    :center (create-identity-table)))

(defn create-my-identity-panel []
  (seesaw-core/horizontal-panel
    :items [ (seesaw-core/border-panel :size [67 :by 15] :east (seesaw-core/label :text (terms/my-identity)))
             [:fill-h 3]
             (seesaw-core/border-panel :size [300 :by 20]
               :west (seesaw-core/text :id :my-identity :text "data" :editable? false))]))

(defn create []
  (seesaw-core/border-panel
    :border 5
    :vgap 5
    :north (create-my-identity-panel)
    :center (create-identity-table-panel)))