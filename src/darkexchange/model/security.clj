(ns darkexchange.model.security
  (:require [clojure.contrib.logging :as logging])
  (:import [org.apache.commons.codec.binary Base64]
           [org.bouncycastle.jce.provider BouncyCastleProvider]
           [java.security KeyFactory KeyPair KeyPairGenerator MessageDigest PrivateKey PublicKey Security Signature]
           [java.security.spec PKCS8EncodedKeySpec X509EncodedKeySpec]
           [java.util Random]
           [javax.crypto Cipher SecretKeyFactory]
           [javax.crypto.spec DESKeySpec]))

(def des-algorithm "DES")

(def default-algorithm "RSA")
(def default-signature-algorithm "SHA1withRSA")
(def default-transformation "RSA/None/NoPadding")
(def default-provider "BC") ; Bouncy Castle provider.
(def default-character-encoding "UTF8")

(Security/addProvider (new BouncyCastleProvider))

(defn generate-key-pair []
  (let [key-pair-generator (KeyPairGenerator/getInstance default-algorithm)]
     (.initialize key-pair-generator 1024)
     (.generateKeyPair key-pair-generator)))

(defn private-key [key-pair]
  (.getPrivate key-pair))

(defn as-private-key [key]
  (cond
    (instance? KeyPair key) (private-key key)
    (instance? PrivateKey key) key
    true (throw (RuntimeException. (str "Don't know how to convert to private key: " key)))))

(defn public-key [key-pair]
  (.getPublic key-pair))

(defn as-public-key [key]
  (cond
    (instance? KeyPair key) (public-key key)
    (instance? PublicKey key) key
    true (throw (RuntimeException. (str "Don't know how to convert to public key: " key)))))

(defn algorithm [key]
  (.getAlgorithm key))

(defn encoded [key]
  (.getEncoded key))

(defn format [key]
  (.getFormat key))

(defn create-cipher 
  ([] (create-cipher default-transformation default-provider))
  ([transformation] (create-cipher transformation default-provider))
  ([transformation provider]
    (Cipher/getInstance transformation provider)))

(defn get-data-bytes [data]
  (if (instance? String data)
    (.getBytes data default-character-encoding)
    data))

(defn get-data-str [data]
  (if (instance? String data)
    data
    (String. data default-character-encoding)))

(defn get-encrypt-key [key]
  (if (instance? KeyPair key)
    (.getPublic key)
    key))

(defn do-cipher [cipher mode key data]
  (.init cipher mode key)
  (.doFinal cipher (get-data-bytes data)))

(defn encrypt 
  ([key data] (encrypt key data (create-cipher)))
  ([key data cipher]
    (do-cipher cipher Cipher/ENCRYPT_MODE (get-encrypt-key key) data)))

(defn get-decrypt-key [key]
  (if (instance? KeyPair key)
    (.getPrivate key)
    key))

(defn decrypt 
  ([key data] (decrypt key data (create-cipher)))
  ([key data cipher]
    (get-data-str (do-cipher cipher Cipher/DECRYPT_MODE (get-decrypt-key key) data))))

; Save and Load keypairs

(defn get-public-key-map [public-key]
  { :algorithm (.getAlgorithm public-key)
    :bytes (.getEncoded (X509EncodedKeySpec. (.getEncoded public-key))) })

(defn get-private-key-map [private-key]
  { :algorithm (.getAlgorithm private-key)
    :bytes (.getEncoded (PKCS8EncodedKeySpec. (.getEncoded private-key))) })

(defn get-key-pair-map [key-pair]
  { :public-key (get-public-key-map (.getPublic key-pair))
    :private-key (get-private-key-map (.getPrivate key-pair))})

(defn decode-public-key [public-key-map]
  (when public-key-map
    (when-let [key-bytes (:bytes public-key-map)]
      (when-let [algorithm (:algorithm public-key-map)]
        (.generatePublic (KeyFactory/getInstance algorithm) (X509EncodedKeySpec. key-bytes))))))

(defn decode-private-key [private-key-map]
  (.generatePrivate (KeyFactory/getInstance (:algorithm private-key-map))
    (PKCS8EncodedKeySpec. (:bytes private-key-map))))

(defn decode-key-pair [key-pair-map]
  (KeyPair. (decode-public-key (:public-key key-pair-map)) (decode-private-key (:private-key key-pair-map))))

; Signing

(defn sign [key data]
  (let [private-key (as-private-key key)
        signature (Signature/getInstance default-signature-algorithm default-provider)]
    (.initSign signature private-key)
    (.update signature (get-data-bytes data))
    (.sign signature)))

(defn verify-signature [key data signature]
  (let [public-key (as-public-key key)
        signature-obj (Signature/getInstance default-signature-algorithm default-provider)]
    (.initVerify signature-obj public-key)
    (.update signature-obj (get-data-bytes data))
    (.verify signature-obj (get-data-bytes signature))))

;http://stackoverflow.com/questions/339004/java-encrypt-decrypt-user-name-and-password-from-a-configuration-file
(defn des-cipher []
  (create-cipher des-algorithm))

(defn des-key-spec [password]
  (DESKeySpec. (get-data-bytes password)))

(defn des-key [password]
  (.generateSecret (SecretKeyFactory/getInstance des-algorithm) (des-key-spec password)))

(defn password-encrypt [password data]
  (encrypt (des-key password) data (des-cipher)))

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
    (.update message-digest (get-data-bytes password-and-salt))
    (Base64/encodeBase64String (.digest message-digest))))