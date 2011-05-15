(ns darkexchange.main
  (:require [darkexchange.core :as core])
  (:gen-class))

(defn -main
  [& args]
  (core/init)
  (require 'darkexchange.controller.main-frame)
  ((ns-resolve (find-ns 'darkexchange.controller.main-frame) 'show)))