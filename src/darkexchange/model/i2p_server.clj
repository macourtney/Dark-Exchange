(ns darkexchange.model.i2p-server
  (:require [clojure.contrib.json :as json]
            [clojure.contrib.logging :as logging]
            [clojure.java.io :as java-io]
            [clojure.java.javadoc :as javadoc]
            [clojure.string :as clj-string])
  (:import [java.io BufferedInputStream BufferedReader BufferedWriter ByteArrayInputStream File FileInputStream InputStreamReader OutputStreamWriter]
           [net.i2p.client.streaming I2PSocketManagerFactory]
           [net.i2p.data Destination PrivateKey PrivateKeyFile]
           [net.i2p.util I2PThread]))

(def manager (atom nil))

(def destination (atom nil))

(def destination-listeners (atom []))

(def save-private-key? (atom false))

(def private-key-file-name "private_key.dat")

(def private-key-file (File. private-key-file-name))

(defn add-destination-listener [listener]
  (swap! destination-listeners conj listener))

(defn set-destination [destination-obj]
  (swap! destination (fn [_] destination-obj)))

(defn current-destination []
  @destination)

(defn base-64-destination []
  (.toBase64 (current-destination)))

(defn private-key-file-exists? []
  (.exists private-key-file))

(defn load-private-key []
  (let [private-key (new PrivateKey)]
    (with-open [key-file-input-stream (FileInputStream. private-key-file)
                key-buffer-input-stream (BufferedInputStream. key-file-input-stream)]
      (.readBytes private-key key-buffer-input-stream))
    private-key))

(defn create-manager-from-saved-key []
  (let [manager (I2PSocketManagerFactory/createManager (ByteArrayInputStream. (.getData (load-private-key))))]
      (if manager
        manager
        (do
          (logging/info "Could not create socket manager from saved private key.")
          (I2PSocketManagerFactory/createManager)))))

(defn create-new-manager []
  (swap! save-private-key? (fn [_] true))
  (I2PSocketManagerFactory/createManager))

(defn create-manager []
  (if (private-key-file-exists?)
    (create-manager-from-saved-key)
    (create-new-manager)))

(defn load-manager []
  (let [new-manager (create-manager)]
    (swap! manager (fn [_] new-manager))
    new-manager))

(defn save-private-key [session]
  (when @save-private-key?
    (logging/debug (str "Saving the private key."))
    (with-open [private-key-output-stream (java-io/make-output-stream private-key-file nil)]
      (.writeBytes (.getDecryptionKey session) private-key-output-stream))))

(defn get-server-socket [manager]
  (try
    (.getServerSocket manager)
    (catch NullPointerException e
      (throw (RuntimeException. "Could not connect to i2p router. Please make sure the i2p router is running." e)))))

(defn read-socket [socket]
  (with-open [reader (java-io/reader (.getInputStream socket))]
    (clj-string/join "\n" (take-while identity (repeatedly #(.readLine reader))))))

(defn write-socket [socket data]
  (with-open [writer (java-io/writer (.getOutputStream socket))]
    (.write writer data)
    (.flush writer)))

(defn read-json [socket]
  (let [json-string (read-socket socket)]
    (logging/debug (str "received: " json-string))
    (json/read-json json-string)))

(defn write-json [socket json-data]
  (let [json-str (json/json-str json-data)]
    (logging/debug (str "sending: " json-str))
    (write-socket socket json-str)))

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
    (println (str "Destination: " (base-64-destination)))
    (start-client-handler client-handler)
    (save-private-key session)
    (notify-destination-listeners)))

(defn init [client-handler]
  (.start (Thread. #(start-server client-handler))))

(defn as-destination [destination]
  (if (instance? Destination destination)
    destination
    (Destination. (str destination))))

(defn send-message [destination data]
  (with-open [socket (.connect @manager (as-destination destination))]
    (write-json socket data)
    (read-json socket)))