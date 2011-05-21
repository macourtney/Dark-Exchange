(ns darkexchange.model.model-init
  (:require [darkexchange.model.property :as property]))

(defn init []
  (property/load-properties))