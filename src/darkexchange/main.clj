(ns darkexchange.main
  (:require [darkexchange.controller.main-frame :as main-frame]
            [darkexchange.core :as core])
  (:gen-class))

(defn -main
  [& args]
  (core/init)
  (main-frame/show))