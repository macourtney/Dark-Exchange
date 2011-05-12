(ns darkexchange.main
  (:require [darkexchange.controller.main-frame :as main-frame])
  (:gen-class))

(defn -main
  [& args]
  (main-frame/show))