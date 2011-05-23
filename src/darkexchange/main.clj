(ns darkexchange.main
  (:require [darkexchange.core :as core])
  (:gen-class))

(defn -main
  [& args]
  (core/init)
  (let [main-frame-ns 'darkexchange.controller.main.main-frame]
    (require main-frame-ns)
    ((ns-resolve (find-ns main-frame-ns) 'show))))