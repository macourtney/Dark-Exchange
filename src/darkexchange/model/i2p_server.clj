(ns darkexchange.model.i2p-server
  (:require [clojure.contrib.logging :as logging]
            [clojure.java.io :as java-io]
            [clojure.java.javadoc :as javadoc])
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

(defn client-handler [server-socket]
  (while true
    (with-open [socket (.accept server-socket)]
      (when socket
        (with-open [reader (BufferedReader. (InputStreamReader. (.getInputStream socket)))
                    writer (BufferedWriter. (OutputStreamWriter. (.getOutputStream socket)))]
          (when-let [line (.readLine reader)]
            (println "Received from client: " line)
            (.write writer line)
            (.flush writer)))))))

(defn start-client-handler []
  (let [server-socket (get-server-socket @manager)
        i2p-thread (I2PThread. #(client-handler server-socket))]
    (.setName i2p-thread "clienthandler1")
    (.setDaemon i2p-thread false)
    (.start i2p-thread)))

(defn start-server []
  (let [new-manager (load-manager)
        session (.getSession new-manager)
        destination-obj (.getMyDestination session)]
    (start-client-handler)
    (println (str "Destination: " (.toBase64 destination-obj)))
    (swap! destination (fn [_] destination-obj))
    (save-private-key session)
    (doseq [destination-listener @destination-listeners]
      (destination-listener destination-obj))))

(defn init []
  (.start (Thread. start-server)))

(defn current-destination []
  @destination)

(defn send-message [destination message]
  (let [destination-address (if (instance? Destination destination) destination (Destination. (str destination)))]
    (let [socket (.connect @manager destination-address)]
      (with-open [writer (BufferedWriter. (OutputStreamWriter. (.getOutputStream socket)))]
        (.write writer message)
        (.flush writer))
      (with-open [reader (BufferedReader. (InputStreamReader. (.getInputStream socket)))]
        (loop [line (.readLine reader)]
          (when line
            (println "Received from server: " line)
            (recur (.readLine reader))))))))