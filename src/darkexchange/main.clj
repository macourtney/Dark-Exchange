(ns darkexchange.main
  (:require [darkexchange.core :as core])
  (:gen-class))

(defn -main
  [& args]
  (core/database-init)
  (let [login-frame-ns 'darkexchange.controller.login.login]
    (require login-frame-ns)
    ((ns-resolve (find-ns login-frame-ns) 'show))))