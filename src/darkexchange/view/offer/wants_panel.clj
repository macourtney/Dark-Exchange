(ns darkexchange.view.offer.wants-panel
  (:require [darkexchange.model.terms :as terms]
            [darkexchange.view.offer.widgets :as offer-widgets]
            [seesaw.core :as seesaw-core]))

(defn create []
  (seesaw-core/horizontal-panel :id :wants-panel :items
    [(terms/i-want) [:fill-h 3] (offer-widgets/create-text :i-want-amount)
     (offer-widgets/create-currency-combobox :i-want-currency) [:fill-h 3] (terms/sent-by) [:fill-h 3]
     (offer-widgets/create-payment-type-combobox :i-want-payment-type)]))