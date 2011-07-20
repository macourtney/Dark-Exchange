(ns darkexchange.controller.main.identity-tab
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as action-utils]
            [darkexchange.controller.identity.view :as identity-view]
            [darkexchange.controller.utils :as controller-utils]
            [darkexchange.controller.widgets.utils :as widgets-utils]
            [darkexchange.model.identity :as identity-model]
            [darkexchange.view.main.identity-tab :as identity-tab-view]
            [seesaw.core :as seesaw-core]
            [seesaw.table :as seesaw-table]))

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

(defn find-my-identity-label [main-frame]
  (seesaw-core/select main-frame ["#my-identity"]))

(defn load-my-identity [main-frame]
  (when-let [my-identity (identity-model/current-user-identity)]
    (.setText (find-my-identity-label main-frame) (identity-model/identity-text my-identity)))
  main-frame)

(defn delete-identity-from-table [main-frame identity]
  (controller-utils/delete-record-from-table (find-identity-table main-frame) (:id identity)))

(defn add-identity-to-table [main-frame identity]
  (when-not (identity-model/is-user-identity? identity)
    (controller-utils/add-record-to-table (find-identity-table main-frame)
      (identity-model/get-table-identity (:id identity)))))

(defn update-identity-id-table [main-frame identity]
  (when-not (identity-model/is-user-identity? identity)
    (controller-utils/update-record-in-table (find-identity-table main-frame)
      (identity-model/get-table-identity (:id identity)))))

(defn attach-identity-listener [main-frame]
  (identity-model/add-identity-add-listener
    (fn [identity] (seesaw-core/invoke-later (add-identity-to-table main-frame identity))))
  (identity-model/add-identity-update-listener
    (fn [identity] (seesaw-core/invoke-later (update-identity-id-table main-frame identity))))
  (identity-model/add-identity-delete-listener
    (fn [identity] (seesaw-core/invoke-later (delete-identity-from-table main-frame identity))))
  main-frame)

(defn find-identity-view-button [main-frame]
  (seesaw-core/select main-frame ["#view-identity-button"]))

(defn attach-view-identity-enable-listener [main-frame]
  (widgets-utils/single-select-table-button (find-identity-view-button main-frame) (find-identity-table main-frame))
  main-frame)

(defn selected-identity-id [main-frame]
  (let [identity-table (find-identity-table main-frame)]
    (:id (seesaw-table/value-at identity-table (.getSelectedRow identity-table)))))

(defn view-identity-action [main-frame e]
  (when-let [identity-id (selected-identity-id main-frame)]
    (identity-view/show main-frame (identity-model/get-record identity-id))))

(defn attach-view-identity-listener [main-frame]
  (action-utils/attach-frame-listener main-frame "#view-identity-button" view-identity-action))

(defn view-identity-if-enabled [main-frame]
  (widgets-utils/do-click-if-enabled (find-identity-view-button main-frame)))

(defn attach-view-identity-table-action [main-frame]
  (widgets-utils/add-table-action (find-identity-table main-frame)
    #(view-identity-if-enabled main-frame))
  main-frame)

(defn load-data [main-frame]
  (load-my-identity (load-identity-table main-frame)))

(defn attach [main-frame]
  (attach-view-identity-table-action
    (attach-view-identity-enable-listener
      (attach-view-identity-listener
        (attach-identity-listener main-frame)))))

(defn init [main-frame]
  (attach (load-data main-frame)))