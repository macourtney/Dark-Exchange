(ns test.darkexchange.util
  (:require [test.init :as test-init]
            [darkexchange.model.user :as user-model]))

(defn login []
  (when-not (user-model/login "test-user" (.toCharArray "password"))
    (throw (RuntimeException. "Failed to login as a test user."))))

(defn logout []
  (user-model/logout))