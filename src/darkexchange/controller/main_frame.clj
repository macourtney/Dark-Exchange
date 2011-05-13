(ns darkexchange.controller.main-frame
  (:require [darkexchange.view.main.main-frame :as view-main-frame]
            [clojure.contrib.logging :as logging]
            [seesaw.core :as seesaw-core])
  (:import [javax.swing JMenuItem JMenu]))

(defn frame-menus [menu-bar]
  (when menu-bar
    (map #(.getMenu menu-bar %)
      (range 0 (.getMenuCount menu-bar)))))

(defn sub-menus [menu]
  (map #(.getItem menu %) (range 0 (.getItemCount menu))))

(defn attach-exit-action [menu]
  (when (= (seesaw-core/id-for menu) "exit-menu-item")
    (seesaw-core/listen menu :action
      (fn [e]
        (let [frame (seesaw-core/to-frame e)]
          (.hide frame)
          (.dispose frame))))))

(defn attach-action [menu]
  (attach-exit-action menu))

(declare attach-actions-to-menus)

(defn attach-action-to-menu [menu]
  (cond
    (instance? JMenu menu) (attach-actions-to-menus (sub-menus menu))
    (instance? JMenuItem menu) (attach-action menu)
    true (throw (RuntimeException. (str "Unknown menu type: " (.getClass menu))))))

(defn attach-actions-to-menus [menus]
  (doseq [menu menus]
    (attach-action-to-menu menu)))

(defn attach-main-menu-actions [frame]
  (attach-actions-to-menus (frame-menus (.getJMenuBar frame))))

(defn show []
  (let [frame (view-main-frame/show)]
    (attach-main-menu-actions frame)))