(ns darkexchange.model.security
  (:require [clojure.contrib.logging :as logging])
  (:import [org.apache.commons.codec.binary Base64]
           [java.security KeyPair KeyPairGenerator MessageDigest]
           [java.util Random]
           [javax.crypto Cipher SecretKeyFactory]
           [javax.crypto.spec DESKeySpec]))

(def des-algorithm "DES")

(def default-algorithm "RSA")
(def default-transformation "RSA/ECB/PKCS1Padding")
(def default-character-encoding "UTF8")

(defn generate-key-pair []
  (let [key-pair-generator (KeyPairGenerator/getInstance default-algorithm)]
     (.initialize key-pair-generator 1024)
     (.generateKeyPair key-pair-generator)))

(defn private-key [key-pair]
  (.getPrivate key-pair))

(defn public-key [key-pair]
  (.getPublic key-pair))

(defn algorithm [key]
  (.getAlgorithm key))

(defn encoded [key]
  (.getEncoded key))

(defn format [key]
  (.getFormat key))

(defn create-cipher 
  ([] (create-cipher default-transformation))
  ([transformation]
    (Cipher/getInstance transformation)))

(defn get-data-bytes [data]
  (if (instance? String data)
    (.getBytes data default-character-encoding)
    data))

(defn get-encrypt-key [key]
  (if (instance? KeyPair key)
    (.getPublic key)
    key))

(defn do-cipher [cipher mode key data]
  (.init cipher mode (get-encrypt-key key))
  (.doFinal cipher (get-data-bytes data)))

(defn encrypt 
  ([key data] (encrypt key data (create-cipher)))
  ([key data cipher]
    (do-cipher cipher Cipher/ENCRYPT_MODE key data)))

(defn get-decrypt-key [key]
  (if (instance? KeyPair key)
    (.getPrivate key)
    key))

(defn decrypt 
  ([key data] (decrypt key data (create-cipher)))
  ([key data cipher]
    (do-cipher cipher Cipher/DECRYPT_MODE key data)))

;http://stackoverflow.com/questions/339004/java-encrypt-decrypt-user-name-and-password-from-a-configuration-file
(defn des-cipher []
  (create-cipher des-algorithm))

(defn des-key-spec [password]
  (DESKeySpec. (get-data-bytes password)))

(defn des-key [password]
  (.generateSecret (SecretKeyFactory/getInstance des-algorithm) (des-key-spec password)))

(defn password-encrypt [password data]
  (encrypt (des-key password) data))

(defn password-decrypt [password data]
  (decrypt (des-key password) data (des-cipher)))

; Basic password protection
(defn
  create-salt []
  (.nextInt (new Random)))

(defn
  encrypt-password-string [password salt]
  (let [message-digest (MessageDigest/getInstance "SHA-1")
        password-and-salt (str password salt)]
    (.update message-digest (.getBytes password-and-salt "iso-8859-1"))
    (Base64/encodeBase64String (.digest message-digest))))