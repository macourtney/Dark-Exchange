(ns darkexchange.uncaught-exception-handler
  (:require [clojure.contrib.logging :as logging]))

(defn init []
  (Thread/setDefaultUncaughtExceptionHandler
    (reify Thread$UncaughtExceptionHandler
      (uncaughtException [this thread throwable]
        (logging/error "Uncaught Exception:" throwable)))))