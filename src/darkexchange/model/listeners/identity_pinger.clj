(ns darkexchange.model.listeners.identity-pinger
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.model.calls.notify :as notify-call]
            [darkexchange.model.peer :as peer-model])
  (:import [java.util Timer TimerTask]))

(def hour-in-milliseconds (long 600000)) ;(long 3600000))

(defn notify-all-peers []
  (logging/info "Notifying peers with pinger.")
  (doseq [peer (peer-model/all-notified-peers)]
    (future (peer-model/notify-destination (:destination peer)))))

(defn create-timer-task []
  (proxy [TimerTask] []
    (run [] (notify-all-peers))))

(defn destination-listener [_]
  (.schedule (Timer. true) (create-timer-task) hour-in-milliseconds hour-in-milliseconds))