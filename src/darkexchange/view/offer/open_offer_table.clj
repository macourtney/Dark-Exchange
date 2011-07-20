(ns darkexchange.view.offer.open-offer-table
  (:require [darkexchange.model.terms :as terms]
            [seesaw.core :as seesaw-core]))

(def table-columns [ { :key :id :text (terms/id) }
                     { :key :i-have-amount :text (terms/i-have-amount) }
                     { :key :i-want-to-send-by :text (terms/i-want-to-send-by) }
                     { :key :i-want-amount :text (terms/i-want-amount) }
                     { :key :i-want-to-receive-by :text (terms/i-want-to-receive-by) }
                     { :key :has-div-wants :text (terms/has-div-wants) }
                     { :key :wants-div-has :text (terms/wants-div-has) } ])

(defn create [{:keys [id preferred-size] :or { id :open-offer-table preferred-size [600 :by 300] }}]
  (seesaw-core/scrollable
    (seesaw-core/table :id id
      :model [ :columns table-columns ])
    :preferred-size preferred-size))