(ns darkexchange.controller.utils
  (:require [clojure.contrib.logging :as logging]
            [seesaw.core :as seesaw-core]))

(defn show [frame]
  (seesaw-core/config! frame :visible? true))