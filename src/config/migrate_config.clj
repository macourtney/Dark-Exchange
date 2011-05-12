(ns config.migrate-config
  (:require [darkexchange.database.migrate :as migrate]))

(defn migrate-config []
   { :directory "/src/darkexchange/database/migrations"
     :init migrate/init
     :ns-content "\n  (:use darkexchange.database.util)"
     :current-version migrate/current-version
     :update-version migrate/update-version })