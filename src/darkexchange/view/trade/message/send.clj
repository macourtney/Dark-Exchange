(ns darkexchange.view.trade.message.send
  (:require [darkexchange.model.terms :as terms]
            [darkexchange.view.utils :as view-utils]
            [seesaw.core :as seesaw-core]))

(defn create-body-text-area []
  (seesaw-core/scrollable (seesaw-core/text :id :message-body-text :multi-line? true :rows 10 :wrap-lines? true)
    :preferred-size [350 :by 200]))

(defn create-body-panel []
  (seesaw-core/border-panel
      :vgap 3
      :north (terms/message-body)
      :center (create-body-text-area)))

(defn create-button-panel []
  (seesaw-core/border-panel
      :border 5
      :hgap 5
      :east (seesaw-core/horizontal-panel :items 
              [ (seesaw-core/button :id :send-button :text (terms/send))
                [:fill-h 3]
                (seesaw-core/button :id :cancel-button :text (terms/done)) ])))

(defn create-content []
  (seesaw-core/border-panel
      :border 5
      :vgap 5
      :center (create-body-panel)
      :south (create-button-panel)))

(defn create [trade-frame]
  (view-utils/center-window-on trade-frame
    (seesaw-core/frame
      :title (terms/send-message)
      :content (create-content)
      :on-close :dispose
      :visible? false)))