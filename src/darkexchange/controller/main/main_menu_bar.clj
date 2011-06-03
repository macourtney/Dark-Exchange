(ns darkexchange.controller.main.main-menu-bar
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.actions.utils :as actions-utils]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing JMenuItem JMenu]))

(defn attach-main-menu-actions [main-frame]
  (actions-utils/attach-window-close-and-exit-listener main-frame "#exit-menu-item"))

(defn init [main-frame]
  (attach-main-menu-actions main-frame))