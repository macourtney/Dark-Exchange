(ns darkexchange.controller.peer-tab
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.i2p-server :as i2p-server]
            [darkexchange.view.main.peer-tab :as peer-tab-view]
            [darkexchange.view.util :as view-util]
            [seesaw.core :as seesaw-core]))

(defn destination-text [main-frame]
  (seesaw-core/select main-frame [:#destination-text]))

(defn set-destination-text [main-frame destination]
  (.setText (destination-text main-frame) (.toBase64 destination)))

(defn create-destination-listener [main-frame]
  (fn [destination]
    (set-destination-text main-frame destination)))

(defn load-destination [main-frame]
  (let [destination (i2p-server/current-destination)]
    (if destination
      (set-destination-text main-frame destination)
      (i2p-server/add-destination-listener (create-destination-listener main-frame)))))