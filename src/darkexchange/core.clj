(ns darkexchange.core
  (:require [clojure.tools.string-utils :as conjure-str-utils]
            [config.environment :as environment]
            [darkexchange.database.util :as database-util]
            [darkexchange.uncaught-exception-handler :as uncaught-exception-handler]
            [drift.runner :as drift-runner]))

(def initialized? (atom false))

(def environment-initialized? (atom false))

(def database-initialized? (atom false))

(def init? (promise))

(defn resolve-fn [ns-symbol fn-symbol]
  (require ns-symbol)
  (ns-resolve (find-ns ns-symbol) fn-symbol))

(defn run-fn [ns-symbol fn-symbol]
  ((resolve-fn ns-symbol fn-symbol)))

(defn environment-init []
  (when (compare-and-set! environment-initialized? false true)
    (uncaught-exception-handler/init)
    (environment/require-environment)
    (database-util/init-database)))

(defn database-init []
  (when (compare-and-set! database-initialized? false true)
    (environment-init)
    (drift-runner/update-to-version Integer/MAX_VALUE)))

(defn
  init-promise-fn []
  (database-init)

  ; Lazy load the following to make sure everything is initialized first.
  (run-fn 'darkexchange.model.model-init 'init)
  (deliver init? true))

(defn
#^{ :doc "Initializes the conjure server." }
  init []
  (when (compare-and-set! initialized? false true)
    (init-promise-fn))
  @init?)

(defn
#^{ :doc "Sets the server mode to the given mode. The given mode must be a keyword or string like development, 
production, or test." }
  set-mode [mode]
  (when mode 
    (environment/set-evironment-property (conjure-str-utils/str-keyword mode))))