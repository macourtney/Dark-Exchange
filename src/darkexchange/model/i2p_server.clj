(ns darkexchange.model.i2p-server
  (:import [net.i2p.client.streaming I2PSocketManagerFactory]))

(def destination (atom nil))

(def destination-listeners (atom []))

(defn add-destination-listener [listener]
  (swap! destination-listeners conj listener))

(defn start-server []
  (let [manager (I2PSocketManagerFactory/createManager)
        serverSocket (.getServerSocket manager)
        session (.getSession manager)
        destination-obj (.getMyDestination session)]
    (println (str "Destination: " (.toBase64 destination-obj)))
    (swap! destination (fn [_] destination-obj))
    (doseq [destination-listener @destination-listeners]
      (destination-listener destination-obj))))

(defn init []
  (.start (Thread. start-server)))

(defn current-destination []
  @destination)