(ns darkexchange.view.trade.view
  (:require [darkexchange.model.terms :as terms]
            [seesaw.core :as seesaw-core]))

(def trade-messages-table-columns [ { :key :id :text (terms/id) }
                                    { :key :from :text (terms/from) }
                                    { :key :date-received :text (terms/date-received) } ])

(defn create-label-value-pair-panel [text label-key]
  (seesaw-core/horizontal-panel
      :items [ (seesaw-core/border-panel :size [130 :by 15] :east (seesaw-core/label :text text))
               [:fill-h 3]
               (seesaw-core/border-panel :size [200 :by 15]
                 :west (seesaw-core/label :id label-key :text "data" :font { :style :plain }))]))

(defn create-trade-id-panel []
  (create-label-value-pair-panel (terms/trade-id) :id))

(defn create-trade-initiated-at-panel []
  (create-label-value-pair-panel (terms/initiated-at) :created_at))

(defn create-trade-partner-panel []
  (create-label-value-pair-panel (terms/trade-partner) :user))

(defn create-partners-trade-id-panel []
  (create-label-value-pair-panel (terms/partners-trade-id) :foreign-trade-id))

(defn create-im-sending-amount-panel []
  (create-label-value-pair-panel (terms/im-sending-amount) :im-sending-amount))

(defn create-im-sending-by-panel []
  (create-label-value-pair-panel (terms/im-sending-by) :im-sending-by))

(defn create-im-receiving-amount-panel []
  (create-label-value-pair-panel (terms/im-receiving-amount) :im-receiving-amount))

(defn create-im-receiving-by-panel []
  (create-label-value-pair-panel (terms/im-receiving-by) :im-receiving-by))

(defn create-waiting-for-panel []
  (create-label-value-pair-panel (terms/waiting-for) :waiting-for))

(defn create-data-panel []
  (seesaw-core/vertical-panel
      :items [ (create-trade-id-panel)
               [:fill-v 3]
               (create-trade-initiated-at-panel)
               [:fill-v 3]
               (create-trade-partner-panel)
               [:fill-v 3]
               (create-partners-trade-id-panel)
               [:fill-v 3]
               (create-im-sending-amount-panel)
               [:fill-v 3]
               (create-im-sending-by-panel)
               [:fill-v 3]
               (create-im-receiving-amount-panel)
               [:fill-v 3]
               (create-im-receiving-by-panel)
               [:fill-v 3]
               (create-waiting-for-panel)]))

(defn create-send-message-button []
  (seesaw-core/button :id :send-message-button :text (terms/send-message)))

(defn create-view-message-button []
  (seesaw-core/button :id :view-message-button :text (terms/view)))

(defn create-trade-messages-title-panel []
  (seesaw-core/border-panel
      :vgap 3
      :west (terms/messages)
      :east (seesaw-core/horizontal-panel :items 
              [ (create-view-message-button)
                [:fill-h 3]
                (create-send-message-button) ])))

(defn create-trade-messages-table []
  (seesaw-core/scrollable
    (seesaw-core/table :id :trade-messages-table
      :model [ :columns trade-messages-table-columns ])
    :preferred-size [350 :by 150]))

(defn create-trade-messages-panel []
  (seesaw-core/border-panel
      :vgap 3
      :north (create-trade-messages-title-panel)
      :center (create-trade-messages-table)))

(defn create-button-panel []
  (seesaw-core/border-panel
      :border 5
      :hgap 5
      :east (seesaw-core/horizontal-panel :items 
              [ (seesaw-core/button :id :reject-button :text (terms/reject-trade) :visible? false)
                [:fill-h 3]
                (seesaw-core/button :id :next-step-button :text "CHANGE ME" :visible? false)
                [:fill-h 3]
                (seesaw-core/button :id :cancel-button :text (terms/cancel)) ])))

(defn create-content []
  (seesaw-core/border-panel
      :border 5
      :vgap 5
      :north (create-data-panel)
      :center (create-trade-messages-panel)
      :south (create-button-panel)))

(defn create []
  (seesaw-core/frame
    :title (terms/trade-viewer)
    :content (create-content)
    :on-close :dispose
    :visible? false))