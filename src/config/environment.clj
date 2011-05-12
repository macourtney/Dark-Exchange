(ns config.environment
  (:require [clojure.contrib.java-utils :as java-utils]
            [clojure.contrib.logging :as logging] 
            [config.environment :as config-env]))

(def initialized (atom false))

(def conjure-environment-property "conjure.environment")
(def default-environment "development")

(defn
  set-evironment-property [environment]
  (java-utils/set-system-properties { conjure-environment-property environment })) 

(defn
  require-environment []
  (when (not (java-utils/get-system-property conjure-environment-property nil))
    (set-evironment-property default-environment))
  (let [mode (java-utils/get-system-property conjure-environment-property nil)]
    (require (symbol (str "config.environments." mode)))))

(defn
#^{ :doc "Returns the name of the environment." }
  environment-name []
  (java-utils/get-system-property conjure-environment-property nil))