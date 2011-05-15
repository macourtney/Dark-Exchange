(ns darkexchange.controller.main-menu-bar
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.window-actions :as window-actions]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing JMenuItem JMenu]))

(defn attach-exit-action [menu]
  (seesaw-core/listen menu :action window-actions/close-window))

(defn attach-main-menu-actions [main-frame]
  (attach-exit-action (seesaw-core/select main-frame ["#exit-menu-item"])))

(defn attach [main-frame]
  (attach-main-menu-actions main-frame))