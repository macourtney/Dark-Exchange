(ns darkexchange.controller.offer.new-offer
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as actions-utils]
            [darkexchange.controller.offer.has-panel :as has-panel]
            [darkexchange.controller.offer.wants-panel :as wants-panel]
            [darkexchange.controller.utils :as controller-utils]
            [darkexchange.model.currency :as currency-model]
            [darkexchange.model.offer :as offer-model]
            [darkexchange.model.payment-type :as payment-type]
            [darkexchange.view.offer.new-offer :as new-offer-view]
            [seesaw.core :as seesaw-core]))

(defn find-cancel-button [new-offer-view]
  (seesaw-core/select new-offer-view ["#cancel-button"]))

(defn attach-cancel-action [new-offer-view]
  (actions-utils/attach-window-close-listener new-offer-view "#cancel-button"))

(defn find-create-offer-button [new-offer-view]
  (seesaw-core/select new-offer-view ["#create-offer-button"]))

(defn has-offer [new-offer-view]
  (has-panel/has-offer new-offer-view))

(defn wants-offer [new-offer-view]
  (wants-panel/wants-offer new-offer-view))

(defn create-new-offer-setup [e [new-offer-view call-back]]
 [new-offer-view call-back (merge (has-offer new-offer-view) (wants-offer new-offer-view))])

(defn create-new-offer [[new-offer-view call-back new-offer]]
  [new-offer-view call-back (offer-model/create-new-offer new-offer)])

(defn create-new-offer-cleanup [[new-offer-view call-back new-offer-id]]
  (call-back new-offer-id)
  (actions-utils/close-window new-offer-view))

(defn attach-create-offer-action [new-offer-view call-back]
  (actions-utils/attach-background-listener new-offer-view "#create-offer-button"
    { :other-params [new-offer-view call-back]
      :before-background create-new-offer-setup
      :background create-new-offer
      :after-background create-new-offer-cleanup }))

(defn attach [new-offer-view call-back]
  (wants-panel/attach (has-panel/attach (attach-create-offer-action (attach-cancel-action new-offer-view) call-back))))

(defn load-data [new-offer-view]
  (wants-panel/load-data (has-panel/load-data new-offer-view)))

(defn show [main-frame call-back]
  (controller-utils/show (attach (load-data (new-offer-view/create main-frame)) call-back)))