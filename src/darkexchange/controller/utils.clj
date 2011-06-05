(ns darkexchange.controller.utils
  (:require [clojure.contrib.logging :as logging]
            [seesaw.core :as seesaw-core]))

(defn find-component [parent-component id]
  (seesaw-core/select parent-component [id]))

(defn show [frame]
  (seesaw-core/config! frame :visible? true))