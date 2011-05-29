(ns darkexchange.model.server
  (:require [darkexchange.model.i2p-server :as i2p-server]))

(def server-receive-interceptors (atom []))

(def server-reply-interceptors (atom []))

(def actions (atom {}))

(defn add-server-receive-interceptor [interceptor]
  (swap! server-receive-interceptors conj interceptor))

(defn add-server-reply-interceptor [interceptor]
  (swap! server-reply-interceptors conj interceptor))

(defn add-action [action-key action-fn]
  (swap! actions assoc action-key action-fn))

(defn run-interceptors [interceptors arg]
  (if (empty? interceptors)
    arg
    ((apply comp interceptors) arg)))

(defn run-server-receive-interceptors [request-map]
  (run-interceptors @server-receive-interceptors request-map))

(defn run-server-reply-interceptors [response-map]
  (run-interceptors @server-reply-interceptors response-map))

(defn action-map []
  @actions)

(defn reset-actions! []
  (reset! actions {}))

(defn find-action [action-key]
  (get (action-map) action-key))

(defn get-action-key [request-map]
  (when-let [action-key (:action request-map)]
    (keyword action-key)))

(defn run-action [request-map]
  (let [action-key (get-action-key request-map)]
    (if-let [action-fn (find-action action-key)]
      (assoc (action-fn request-map) :action action-key)
      { :data nil :type :action-not-found :action action-key })))

(defn build-request [socket]
  (run-server-receive-interceptors (i2p-server/read-json socket)))

(defn build-response [request-map]
  (run-server-reply-interceptors (run-action request-map)))

(defn perform-action [socket]
  (i2p-server/write-json socket (build-response (build-request socket))))

(defn client-handler [server-socket]
  (while true
    (let [socket (.accept server-socket)]
      (when socket
        (perform-action socket)))))

(defn init []
  (i2p-server/init client-handler))

(defn header-reply-interceptor [response-map]
  (merge { :destination (i2p-server/base-64-destination) :type :ok } response-map))

(add-server-reply-interceptor header-reply-interceptor)