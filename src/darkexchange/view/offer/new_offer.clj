(ns darkexchange.view.offer.new-offer
  (:require [darkexchange.model.terms :as terms]
            [darkexchange.view.offer.has-panel :as has-panel]
            [darkexchange.view.offer.wants-panel :as wants-panel]
            [darkexchange.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(defn create-button-panel []
  (seesaw-core/border-panel :east
    (seesaw-core/horizontal-panel :items
      [(seesaw-core/button :id :create-offer-button :text (terms/create-offer))
       [:fill-h 3]
       (seesaw-core/button :id :cancel-button :text (terms/cancel))])))

(defn create-content []
  (seesaw-core/vertical-panel
    :border 5
    :items [(has-panel/create) [:fill-v 5] (wants-panel/create) [:fill-v 5] (create-button-panel)]))

(defn create [main-frame]
  (view-utils/center-window-on main-frame
    (seesaw-core/frame
      :title (terms/new-offer)
      :content (create-content)
      :on-close :dispose
      :visible? false)))