(ns darkexchange.controller.main.identity-tab
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.utils :as controller-utils]
            [darkexchange.model.identity :as identity-model]
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

(defn delete-identity-from-table [main-frame identity]
  (controller-utils/delete-record-from-table (find-identity-table main-frame) (:id identity)))

(defn add-identity-to-table [main-frame identity]
  (controller-utils/add-record-to-table (find-identity-table main-frame)
    (identity-model/get-table-identity (:id identity))))

(defn update-identity-id-table [main-frame identity]
  (controller-utils/update-record-in-table (find-identity-table main-frame)
    (identity-model/get-table-identity (:id identity))))

(defn attach-identity-listener [main-frame]
  (identity-model/add-identity-add-listener
    (fn [identity] (seesaw-core/invoke-later (add-identity-to-table main-frame identity))))
  (identity-model/add-identity-update-listener
    (fn [identity] (seesaw-core/invoke-later (update-identity-id-table main-frame identity))))
  (identity-model/add-identity-delete-listener
    (fn [identity] (seesaw-core/invoke-later (delete-identity-from-table main-frame identity))))
  main-frame)

(defn load-data [main-frame]
  (load-identity-table main-frame))

(defn attach [main-frame]
  (attach-identity-listener main-frame))

(defn init [main-frame]
  (attach (load-data main-frame)))