(ns darkexchange.view.offer.new-offer
  (:require [darkexchange.model.terms :as terms]
            [seesaw.core :as seesaw-core]))

(defn create-text [id]
  (seesaw-core/text :id id :preferred-size [100 :by 25]))

(defn create-currency-combobox [id]
  (seesaw-core/combobox :id id :preferred-size [120 :by 25]))

(defn create-payment-type-combobox [id]
  (seesaw-core/combobox :id id :preferred-size [180 :by 25]))

(defn create-has-panel []
  (seesaw-core/horizontal-panel :items
    [(terms/i-have) [:fill-h 3] (create-text :i-have-amount) (create-currency-combobox :i-have-currency) [:fill-h 3]
     (terms/to-send-by) [:fill-h 3] (create-payment-type-combobox :i-have-payment-type)]))

(defn create-wants-panel []
  (seesaw-core/horizontal-panel :items
    [(terms/i-want) [:fill-h 3] (create-text :i-want-amount) (create-currency-combobox :i-want-currency) [:fill-h 3]
     (terms/sent-by) [:fill-h 3] (create-payment-type-combobox :i-want-payment-type)]))

(defn create-button-panel []
  (seesaw-core/border-panel :east
    (seesaw-core/horizontal-panel :items
      [(seesaw-core/button :id :create-offer-button :text (terms/create-offer))
       [:fill-h 3]
       (seesaw-core/button :id :cancel-button :text (terms/cancel))])))

(defn create-content []
  (seesaw-core/vertical-panel
    :border 5
    :items [(create-has-panel) [:fill-v 5] (create-wants-panel) [:fill-v 5] (create-button-panel)]))

(defn show []
  (seesaw-core/frame
    :title (terms/new-offer)
    :content (create-content)
    :on-close :dispose))