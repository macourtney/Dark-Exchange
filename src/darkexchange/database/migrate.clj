(ns darkexchange.database.migrate
  (:require [clojure.contrib.command-line :as command-line]
            [clojure.contrib.logging :as logging]
            [clojure.tools.string-utils :as string-utils]
            [darkexchange.core :as core]
            [darkexchange.database.util :as database-util]))

(def schema-info-table "schema_info")
(def version-column :version)

(defn init [args]
  (command-line/with-command-line args
    "lein migrate [options]"
    [ [version "The version to migrate to. Example: -version 2 -> migrates to version 2." nil]
      [mode "The server mode. For example, development, production, or test." nil]
      remaining]
  
    (core/set-mode mode)
    (core/init)))

(defn
  version-table-is-empty []
  (logging/info (str schema-info-table " is empty. Setting the initial version to 0."))
  (database-util/insert-into schema-info-table { version-column 0 })
  0)

(defn
  version-table-exists []
  (logging/info (str schema-info-table " exists"))
  (if-let [version-result-map (first (database-util/sql-find { :table schema-info-table :limit 1 }))]
    (get version-result-map version-column)
    (version-table-is-empty)))

(defn
  version-table-does-not-exist []
  (logging/info (str schema-info-table " does not exist. Creating table..."))
  (database-util/create-table schema-info-table 
    (database-util/integer (string-utils/str-keyword version-column) { :not-null true }))
  (version-table-is-empty))

(defn 
#^{:doc "Gets the current db version number. If the schema info table doesn't exist this function creates it. If the 
schema info table is empty, then it adds a row and sets the version to 0."}
  current-version []
  (if (database-util/table-exists? schema-info-table)
    (version-table-exists)
    (version-table-does-not-exist)))

(defn
#^{:doc "Updates the version number saved in the schema table in the database."}
  update-version [new-version]
  (database-util/update schema-info-table ["true"] { version-column new-version }))