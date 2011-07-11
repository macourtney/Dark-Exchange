(ns darkexchange.controller.actions.utils
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.utils :as controller-utils]
            [seesaw.core :as seesaw-core]))

(defn attach-listener [parent-component id listener]
  (seesaw-core/listen (controller-utils/find-component parent-component id)
    :action listener)
  parent-component)

(defn frame-listener [listener e]
  (listener (seesaw-core/to-frame e) e))

(defn attach-frame-listener [parent-component id listener]
  (attach-listener parent-component id #(frame-listener listener %)))

(defn close-window
  ([frame e] (close-window frame))
  ([frame]
    (.hide frame)
    (.dispose frame)))

(defn attach-window-close-listener [parent-component id]
  (attach-frame-listener parent-component id close-window))

(defn close-window-and-exit [frame e]
  (close-window frame e)
  (System/exit 0))

(defn attach-window-close-and-exit-listener [parent-component id]
  (attach-frame-listener parent-component id close-window-and-exit))

(defn after-background-runner [parent-component after-background background-result]
  (try
    (when after-background
      (after-background background-result))
    (finally
      (controller-utils/enable-widget parent-component))))

(defn background-runner [parent-component background after-background before-background-result]
  (try
    (let [background-result (background before-background-result)]
      (seesaw-core/invoke-later (after-background-runner parent-component after-background background-result)))
    (catch Throwable t
      (seesaw-core/invoke-later (controller-utils/enable-widget parent-component))
      (throw t))))

(defn create-background-listener [before-background background after-background other-params]
  (fn [e]
    (let [parent-component (seesaw-core/to-frame e)]
      (try
        (controller-utils/disable-widget parent-component)
        (let [before-background-result (if before-background (before-background e other-params) [e other-params])]
          (.start (Thread. #(background-runner parent-component background after-background
                              before-background-result))))
        (catch Throwable t
          (controller-utils/enable-widget parent-component)
          (throw t))))))

(defn attach-background-listener [parent-component id {:keys [before-background background after-background
                                                              other-params]
                                                       :or { before-background nil, after-background nil,
                                                             other-params nil }}]
    (attach-listener parent-component id (create-background-listener before-background background after-background
                                           other-params)))