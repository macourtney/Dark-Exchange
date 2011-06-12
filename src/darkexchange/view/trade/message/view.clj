(ns darkexchange.view.trade.message.view
  (:require [darkexchange.model.terms :as terms]
            [seesaw.core :as seesaw-core]))

(defn create-body-text-area []
  (seesaw-core/scrollable (seesaw-core/text :id :message-body-text :multi-line? true :rows 10 :wrap-lines? true
                            :editable? false)
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
      :east (seesaw-core/button :id :cancel-button :text (terms/cancel))))

(defn create-content []
  (seesaw-core/border-panel
      :border 5
      :vgap 5
      :center (create-body-panel)
      :south (create-button-panel)))

(defn create []
  (seesaw-core/frame
    :title (terms/message-viewer)
    :content (create-content)
    :on-close :dispose
    :visible? false))