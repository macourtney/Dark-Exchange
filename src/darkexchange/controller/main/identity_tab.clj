(ns darkexchange.controller.main.identity-tab
  (:require [darkexchange.model.identity :as identity-model]
            [darkexchange.view.main.identity-tab :as identity-tab-view]
            [seesaw.core :as seesaw-core]))

(defn find-identity-table [main-frame]
  (seesaw-core/select main-frame ["#identity-table"]))

(defn reload-table-data [main-frame]
  (when-let [identities (identity-model/table-identities)]
    (seesaw-core/config! (find-identity-table main-frame)
      :model [:columns identity-tab-view/identity-table-columns
              :rows identities])))

(defn load-identity-table [main-frame]
  (reload-table-data main-frame)
  main-frame)

(defn load-data [main-frame]
  (load-identity-table main-frame))

(defn attach [main-frame]
  ;(attach-peer-listener (attach-listener-to-add-button main-frame))
  main-frame)

(defn init [main-frame]
  (attach (load-data main-frame)))