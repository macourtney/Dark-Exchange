(ns darkexchange.main
  (:require [clojure.contrib.command-line :as command-line]
            [darkexchange.core :as core])
  (:gen-class))

(defn -main
  [& args]
  (command-line/with-command-line args
    "run.bat [options] or run.sh [options]"
    [ [mode "The run mode. For example, development, production, or test. The default is production." "production"]
      remaining]

    (core/set-mode mode)
    (core/database-init)
    (let [login-frame-ns 'darkexchange.controller.login.login]
      (require login-frame-ns)
      ((ns-resolve (find-ns login-frame-ns) 'show)))))