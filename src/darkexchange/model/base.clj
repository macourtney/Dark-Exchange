(ns darkexchange.model.base
  (:require [darkexchange.database.util :as database-util]))

(def db (deref database-util/db))