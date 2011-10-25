(ns darkexchange.view.identity.view
  (:require [darkexchange.model.terms :as terms]
            [darkexchange.view.offer.open-offer-table :as open-offer-table-view]
            [darkexchange.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(defn create-label-value-pair-panel [text label-key]
  (seesaw-core/horizontal-panel
      :items [ (seesaw-core/border-panel :size [150 :by 15] :east (seesaw-core/label :text text))
               [:fill-h 3]
               (seesaw-core/border-panel :size [200 :by 15]
                 :west (seesaw-core/label :id label-key :text "data" :font { :style :plain }))]))

(defn create-name-panel []
  (create-label-value-pair-panel (terms/name) :name-label))

(defn create-public-key-panel []
  (create-label-value-pair-panel (terms/public-key) :public-key-label))

(defn create-algorithm-panel []
  (create-label-value-pair-panel (terms/algorithm) :public-key-algorithm-label))

(defn create-is-online-panel []
  (create-label-value-pair-panel (terms/is-online) :is-online-label))

(defn create-my-trust-score-panel []
  (create-label-value-pair-panel (terms/my-trust-score) :my-trust-score-label))

(defn create-network-trust-score-panel []
  (create-label-value-pair-panel (terms/network-trust-score) :network-trust-score-label))

(defn trust-score-slider-panel []
  (seesaw-core/border-panel
      :border 5
      :hgap 5
      :west (terms/completely-distrust)
      :center (seesaw-core/slider :id :trust-score-slider :value 0 :min -100 :max 100 :orientation :horizontal)
      :east (terms/completely-trust)))

(defn create-data-panel []
  (seesaw-core/vertical-panel
    :items [ (create-name-panel)
             [:fill-v 3]
             (create-public-key-panel)
             [:fill-v 3]
             (create-algorithm-panel)
             [:fill-v 3]
             (create-is-online-panel)
             [:fill-v 3]
             (create-my-trust-score-panel)
             [:fill-v 3]
             (create-network-trust-score-panel)
             [:fill-v 3]
             (trust-score-slider-panel)]))

(defn create-offer-table-title []
  (seesaw-core/horizontal-panel
    :items [ (terms/offers)
             [:fill-h 3]
             (seesaw-core/label :id :offer-table-status-label :text (terms/status-parens (terms/loading)))]))

(defn create-offer-table-button-panel []
  (seesaw-core/horizontal-panel
    :items [ (seesaw-core/button :id :view-offer-button :text (terms/view) :enabled? false) ]))

(defn create-offer-table-header []
  (seesaw-core/border-panel
      :border 5
      :hgap 5
      :west (create-offer-table-title)
      :east (create-offer-table-button-panel)))

(defn create-offer-table-panel []
  (seesaw-core/border-panel
      :border 5
      :hgap 5
      :north (create-offer-table-header)
      :center (open-offer-table-view/create { :id :open-offer-table })))

(defn create-center-panel []
  (seesaw-core/border-panel
      :border 5
      :hgap 5
      :north (seesaw-core/border-panel :border 0 :west (create-data-panel))
      :center (create-offer-table-panel)))

(defn create-button-panel []
  (seesaw-core/border-panel
      :border 5
      :hgap 5
      :east (seesaw-core/horizontal-panel :items 
              [ (seesaw-core/button :id :cancel-button :text (terms/done)) ])))

(defn create-content []
  (seesaw-core/border-panel
      :border 5
      :vgap 5
      :center (create-center-panel)
      :south (create-button-panel)))

(defn create [main-frame]
  (view-utils/center-window-on main-frame
    (seesaw-core/frame
      :title (terms/offer-view)
      :content (create-content)
      :on-close :dispose
      :visible? false)))