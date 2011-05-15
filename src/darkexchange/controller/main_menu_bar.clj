(ns darkexchange.controller.main-menu-bar
  (:require [clojure.contrib.logging :as logging]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing JMenuItem JMenu]))

(defn attach-exit-action [menu]
  (seesaw-core/listen menu :action
    (fn [e]
      (let [frame (seesaw-core/to-frame e)]
        (.hide frame)
        (.dispose frame)))))

(defn attach-main-menu-actions [main-frame]
  (attach-exit-action (seesaw-core/select main-frame ["#exit-menu-item"])))

(defn attach [main-frame]
  (attach-main-menu-actions main-frame))