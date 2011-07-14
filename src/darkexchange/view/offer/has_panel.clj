(ns darkexchange.view.offer.has-panel
  (:require [darkexchange.model.terms :as terms]
            [darkexchange.view.offer.widgets :as offer-widgets]
            [seesaw.core :as seesaw-core]))

(defn create []
  (seesaw-core/horizontal-panel :id :has-panel :items
    [(terms/i-have) [:fill-h 3] (offer-widgets/create-text :i-have-amount)
     (offer-widgets/create-currency-combobox :i-have-currency) [:fill-h 3] (terms/to-send-by) [:fill-h 3]
     (offer-widgets/create-payment-type-combobox :i-have-payment-type)]))