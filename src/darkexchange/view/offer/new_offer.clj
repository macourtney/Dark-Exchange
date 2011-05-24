(ns darkexchange.view.offer.new-offer
  (:require [darkexchange.model.terms :as terms]
            [seesaw.core :as seesaw-core]))

(defn create-has-panel []
  (seesaw-core/horizontal-panel :items
    [(terms/i-have) (seesaw-core/text :id :i-have-amount) (seesaw-core/combobox :id :i-have-currency) (terms/to-send-by)
     (seesaw-core/combobox :id :i-have-payment-type)]))

(defn create-wants-panel []
  (seesaw-core/horizontal-panel :items
    [(terms/i-want) (seesaw-core/text :id :i-want-amount) (seesaw-core/combobox :id :i-want-currency) (terms/sent-by)
     (seesaw-core/combobox :id :i-want-payment-type)]))

(defn create-button-panel []
  (seesaw-core/border-panel :east
    (seesaw-core/horizontal-panel :items
      [(seesaw-core/button :id :create-offer-button :text (terms/create-offer))
       (seesaw-core/button :id :cancel-button :text (terms/cancel))])))

(defn create-content []
  (seesaw-core/vertical-panel
    :border 5
    :items [(create-has-panel) [:fill-v 3] (create-wants-panel) [:fill-v 5] (create-button-panel)]))

(defn show []
  (seesaw-core/frame
    :title (terms/new-offer)
    :content (create-content)
    :on-close :dispose))