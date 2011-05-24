(ns darkexchange.controller.offer.new-offer
  (:require [darkexchange.controller.actions.window-actions :as window-actions]
            [darkexchange.view.offer.new-offer :as new-offer-view]
            [seesaw.core :as seesaw-core]))

(defn find-cancel-button [new-offer-view]
  (seesaw-core/select new-offer-view ["#cancel-button"]))

(defn attach-cancel-action [new-offer-view]
  (seesaw-core/listen (find-cancel-button new-offer-view)
    :action window-actions/close-window))

(defn attach [new-offer-view]
  (attach-cancel-action new-offer-view))

(defn show [call-back]
  (attach (new-offer-view/show)))