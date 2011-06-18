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

(defn scrape-offer [new-offer-view]
  (offer-model/create-new-offer (merge (has-offer new-offer-view) (wants-offer new-offer-view))))

(defn attach-create-offer-action [new-offer-view call-back]
  (actions-utils/attach-frame-listener new-offer-view "#create-offer-button"
    (fn [frame e] (call-back (scrape-offer new-offer-view)) (actions-utils/close-window frame e))))

(defn attach [new-offer-view call-back]
  (attach-create-offer-action (attach-cancel-action new-offer-view) call-back))

(defn load-data [new-offer-view]
  (wants-panel/load-data (has-panel/load-data new-offer-view)))

(defn show [main-frame call-back]
  (controller-utils/show (attach (load-data (new-offer-view/create main-frame)) call-back)))