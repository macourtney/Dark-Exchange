(ns darkexchange.controller.main.main-menu-bar
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as actions-utils]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing JMenuItem JMenu]))

(defn attach-exit-action [menu]
  (seesaw-core/listen menu :action actions-utils/close-window-and-exit))

(defn attach-main-menu-actions [main-frame]
  (attach-exit-action (seesaw-core/select main-frame ["#exit-menu-item"])))

(defn attach [main-frame]
  (attach-main-menu-actions main-frame))