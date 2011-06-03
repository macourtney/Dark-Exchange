(ns darkexchange.model.i2p-server
  (:require [clojure.contrib.json :as json]
            [clojure.contrib.logging :as logging]
            [clojure.java.io :as java-io]
            [clojure.java.javadoc :as javadoc]
            [clojure.string :as clj-string])
  (:import [java.io BufferedInputStream BufferedReader BufferedWriter ByteArrayInputStream File FileInputStream InputStreamReader OutputStreamWriter PrintWriter]
           [net.i2p.client.streaming I2PSocketManagerFactory]
           [net.i2p.data Destination PrivateKey PrivateKeyFile]
           [net.i2p.util I2PThread]))

(def manager (atom nil))

(def destination (atom nil))

(def destination-listeners (atom []))

(def send-message-fail-listeners (atom []))

(def private-key-file-name "private_key.dat")

(def private-key-directory-name "data")

(def private-key-file (File. private-key-directory-name private-key-file-name))

(defn add-destination-listener [listener]
  (swap! destination-listeners conj listener))

(defn add-send-message-fail-listener [listener]
  (swap! send-message-fail-listeners conj listener))

(defn set-destination [destination-obj]
  (swap! destination (fn [_] destination-obj)))

(defn current-destination []
  @destination)

(defn base-64-destination []
  (when-let [destination (current-destination)]
    (.toBase64 destination)))

(defn private-key-file-exists? []
  (.exists private-key-file))

(defn load-private-key []
  (let [private-key (new PrivateKey)]
    (with-open [key-file-input-stream (FileInputStream. private-key-file)
                key-buffer-input-stream (BufferedInputStream. key-file-input-stream)]
      (.readBytes private-key key-buffer-input-stream))
    private-key))

(defn save-private-key []
  (when (not (private-key-file-exists?))
    (logging/debug (str "Creating and saving the private key."))
    (PrivateKeyFile/main (into-array String (list (.getPath private-key-file))))))

(defn create-new-manager []
  (I2PSocketManagerFactory/createManager (java-io/input-stream private-key-file)))

(defn create-manager []
  (save-private-key)
  (create-new-manager))

(defn load-manager []
  (let [new-manager (create-manager)]
    (reset! manager new-manager)
    new-manager))

(defn get-server-socket [manager]
  (try
    (.getServerSocket manager)
    (catch NullPointerException e
      (throw (RuntimeException. "Could not connect to i2p router. Please make sure the i2p router is running." e)))))

(defn read-socket [socket]
  (let [reader (java-io/reader (.getInputStream socket))]
    (clj-string/join "\n" (take-while identity (repeatedly #(.readLine reader))))))

(defn write-socket [socket data]
  (let [writer (java-io/writer (.getOutputStream socket))]
    (.write writer data)
    (.flush writer)))

(defn read-json [socket]
  (json/read-json (java-io/reader (.getInputStream socket))))

(defn write-json [socket json-data]
  (let [socket-writer (PrintWriter. (.getOutputStream socket))]
    (json/write-json json-data socket-writer)
    (.flush socket-writer)))

(defn start-client-handler [client-handler]
  (let [server-socket (get-server-socket @manager)
        i2p-thread (I2PThread. #(client-handler server-socket))]
    (.setName i2p-thread "clienthandler1")
    (.setDaemon i2p-thread false)
    (.start i2p-thread)))

(defn notify-destination-listeners []
  (doseq [destination-listener @destination-listeners]
    (destination-listener (current-destination))))

(defn start-server [client-handler]
  (let [new-manager (load-manager)
        session (.getSession new-manager)]
    (set-destination (.getMyDestination session))
    (start-client-handler client-handler)
    (notify-destination-listeners)))

(defn init [client-handler]
  (.start (Thread. #(start-server client-handler))))

(defn as-destination [destination]
  (if (instance? Destination destination)
    destination
    (Destination. (str destination))))

(defn as-destination-str [destination]
  (if (instance? Destination destination)
    (.toBase64 destination)
    destination))

(defn notify-send-message-fail [destination data]
  (let [destination-obj (as-destination destination)]
    (doseq [send-message-fail-listener @send-message-fail-listeners]
      (send-message-fail-listener destination data))))

(defn send-message [destination data]
  (let [destination-obj (as-destination destination)]
    (when @manager
      (if (.ping @manager destination-obj 30000)
        (let [socket (.connect @manager destination-obj)]
          (write-json socket data)
          (read-json socket))
        (notify-send-message-fail destination-obj data)))))