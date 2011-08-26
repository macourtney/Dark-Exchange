(ns darkexchange.development-main
  (:require [clojure.contrib.command-line :as command-line]
            [darkexchange.main :as main]))

(defn -main [& args]
  (command-line/with-command-line args
    "lein run :development [options]"
    [ [mode "The run mode. For example, development, production, or test. The default is development." "development"]
      remaining]

    (apply main/-main "-mode" mode remaining)
    @(promise)))