(ns darkexchange.model.security
  (:require [clojure.contrib.logging :as logging])
  (:import [org.apache.commons.codec.binary Base64]
           [org.bouncycastle.jce.provider BouncyCastleProvider]
           [java.security KeyFactory KeyPair KeyPairGenerator MessageDigest PrivateKey PublicKey Security Signature]
           [java.security.spec PKCS8EncodedKeySpec X509EncodedKeySpec]
           [java.util Random]
           [javax.crypto Cipher SecretKeyFactory]
           [javax.crypto.spec SecretKeySpec]))

(def des-algorithm "DES")

(def default-symmetrical-algorithm des-algorithm)

(def default-algorithm "RSA")
(def default-signature-algorithm "SHA1withRSA")
(def default-transformation "RSA/None/NoPadding")
(def default-provider "BC") ; Bouncy Castle provider.
(def default-character-encoding "UTF8")

(def default-encrypt-password-algorithm "SHA-256")
(def default-encrypt-password-n 1000)

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

(defn integer-byte [integer byte-offset]
  (let [short-int (bit-and 0xff (bit-shift-right integer (* byte-offset 8)))]
    (if (< short-int 128)
      (byte short-int)
      (byte (- short-int 256)))))

(defn integer-bytes [integer]
  (byte-array [(integer-byte integer 3) (integer-byte integer 2) (integer-byte integer 1) (integer-byte integer 0)]))

(defn get-data-bytes [data]
  (cond
    (instance? String data) (.getBytes data default-character-encoding)
    (instance? Integer data) (integer-bytes data)
    true data))

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
(defn des-cipher
  ([] (des-cipher default-symmetrical-algorithm))
  ([algorithm]
    (create-cipher algorithm)))

(defn prepare-password [password]
  (let [message-digest (MessageDigest/getInstance default-encrypt-password-algorithm)]
    (.reset message-digest)
    (into-array Byte/TYPE (take 8 (.digest message-digest (get-data-bytes password))))))

(defn key-spec [password algorithm]
  (SecretKeySpec. (prepare-password password) algorithm))

(defn des-key
  ([password] (des-key password default-symmetrical-algorithm))
  ([password algorithm]
    (.generateSecret (SecretKeyFactory/getInstance algorithm) (key-spec password algorithm))))

(defn password-encrypt
  ([password data] (password-encrypt password data default-symmetrical-algorithm))
  ([password data algorithm]
    (encrypt (des-key password algorithm) data (des-cipher algorithm))))

(defn password-decrypt
  ([password data] (password-decrypt password data default-symmetrical-algorithm))
  ([password data algorithm]
    (decrypt (des-key password algorithm) data (des-cipher algorithm))))

; Basic password protection
(defn
  create-salt []
  (.nextInt (new Random)))

(defn
  encrypt-password-string
  ([password salt] (encrypt-password-string password salt default-encrypt-password-algorithm default-encrypt-password-n))
  ([password salt algorithm n]
    (let [message-digest (MessageDigest/getInstance algorithm)]
      (.reset message-digest)
      (.update message-digest (get-data-bytes salt))
      (Base64/encodeBase64String
        (reduce (fn [data _] (.reset message-digest) (.digest message-digest data))
          (get-data-bytes password) (range n))))))