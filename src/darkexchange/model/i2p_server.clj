(ns darkexchange.model.i2p-server
  (:require [clojure.contrib.logging :as logging]
            [clojure.java.io :as java-io]
            [clojure.java.javadoc :as javadoc])
  (:import [java.io BufferedInputStream ByteArrayInputStream File FileInputStream]
           [net.i2p.client.streaming I2PSocketManagerFactory]
           [net.i2p.data PrivateKey PrivateKeyFile]))

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

(defn start-server []
  (let [manager (create-manager)
        serverSocket (get-server-socket manager)
        session (.getSession manager)
        destination-obj (.getMyDestination session)]
    (println (str "Destination: " (.toBase64 destination-obj)))
    (swap! destination (fn [_] destination-obj))
    (save-private-key session)
    (doseq [destination-listener @destination-listeners]
      (destination-listener destination-obj))))

(defn init []
  (.start (Thread. start-server)))

(defn current-destination []
  @destination)