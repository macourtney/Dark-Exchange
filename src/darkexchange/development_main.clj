(ns darkexchange.development-main
  (:require [darkexchange.core :as core]
            [darkexchange.main :as main]))

(defn -main [& args]
  (core/set-mode "development")
  (main/-main))