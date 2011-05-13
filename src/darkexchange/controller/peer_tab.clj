(ns darkexchange.controller.peer-tab
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.i2p-server :as i2p-server]
            [darkexchange.view.main.peer-tab :as peer-tab-view]
            [seesaw.core :as seesaw-core]))

(defn tabbed-pane [frame]
  (.getContentPane frame))

(defn tab-pair [tabbed-pane-obj index]
  (list (.getTitleAt tabbed-pane-obj index) (.getComponentAt tabbed-pane-obj index)))

(defn all-main-tabs-list [frame]
  (let [tabbed-pane-obj (tabbed-pane frame)]
    (map #(tab-pair tabbed-pane-obj %)
      (range (.getTabCount tabbed-pane-obj)))))

(defn peer-tab [frame]
  (let [peer-tab-obj (some #(if (= (first %1) peer-tab-view/tab-name) (second %1)) (all-main-tabs-list frame))]
    (logging/debug (str "peer-tab-obj: " peer-tab-obj))
    (logging/debug (str "(.getName peer-tab-obj): " (.getName peer-tab-obj)))
    peer-tab-obj))

(defn destination-text [frame]
  (seesaw-core/select (peer-tab frame) [:#destination-text]))

(defn set-destination-text [frame destination]
  (.setText (destination-text frame) (.toBase64 destination)))

(defn create-destination-listener [frame]
  (fn [destination]
    (set-destination-text frame destination)))

(defn load-destination [frame]
  (let [destination (i2p-server/current-destination)]
    (if destination
      (set-destination-text frame destination)
      (i2p-server/add-destination-listener (create-destination-listener frame)))))