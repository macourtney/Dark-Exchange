(ns darkexchange.model.payment-type
  (:require [darkexchange.model.currency :as currency]
            [darkexchange.model.terms :as terms]))

(def checkable-currencies ["AED" "AFN" "ALL" "AMD" "ANG" "AOA" "ARS" "AUD" "AWG" "AZN" "BAM" "BBD" "BDT" "BGN" "BHD"
                           "BIF" "BMD" "BND" "BOB" "BRL" "BSD" "BTN" "BWP" "BYR" "BZD" "CAD" "CDF" "CHF" "CLP" "CNY"
                           "COP" "CRC" "CUP" "CVE" "CZK" "DJF" "DKK" "DOP" "DZD" "EGP" "ERN" "ETB" "EUR" "FJD" "FKP"
                           "GBP" "GEL" "GGP" "GHS" "GIP" "GMD" "GNF" "GTQ" "GYD" "HKD" "HNL" "HRK" "HTG" "HUF" "IDR"
                           "ILS" "IMP" "INR" "IQD" "IRR" "ISK" "JEP" "JMD" "JOD" "JPY" "KES" "KGS" "KHR" "KMF" "KPW"
                           "KRW" "KWD" "KYD" "KZT" "LAK" "LBP" "LKR" "LRD" "LSL" "LTL" "LVL" "LYD" "MAD" "MDL" "MGA"
                           "MKD" "MMK" "MNT" "MOP" "MRO" "MUR" "MVR" "MWK" "MXN" "MYR" "MZN" "NAD" "NGN" "NIO" "NOK"
                           "NPR" "NZD" "OMR" "PAB" "PEN" "PGK" "PHP" "PKR" "PLN" "PYG" "QAR" "RON" "RSD" "RUB" "RWF"
                           "SAR" "SBD" "SCR" "SDG" "SEK" "SGD" "SHP" "SLL" "SOS" "SPL" "SRD" "STD" "SVC" "SYP" "SZL"
                           "THB" "TJS" "TMM" "TND" "TOP" "TRY" "TTD" "TVD" "TWD" "TZS" "UAH" "UGX" "USD" "UYU" "UZS"
                           "VEF" "VND" "VUV" "WST" "XAF" "XCD" "XOF" "XPF" "YER" "ZAR" "ZMK" "ZWD"])

(def mailable-currencies (concat checkable-currencies ["XAG" "XAU" "XPD" "XPT"]))

(def payment-types [{ :code "BITCOIN" :name (terms/bitcoin-transfer) :currency-types ["BITCOIN"] }
                    { :code "CAM" :name (terms/cash-by-mail) :currency-types mailable-currencies }
                    { :code "CHM" :name (terms/check-or-money-order-by-mail) :currency-types checkable-currencies }
                    { :code "DWOLLA" :name (terms/dwolla) :currency-types ["USD"] }
                    { :code "LR" :name (terms/liberty-reserve) :currency-types ["USD" "EUR" "XAUG"] }
                    { :code "NAMECOIN" :name (terms/namecoin-transfer) :currency-types ["NAMECOIN"] }
                    { :code "PAXUM" :name (terms/paxum) :currency-types ["USD"] }
                    { :code "PAYPAL" :name (terms/paypal) :currency-types ["AUD" "BRL" "CAD" "CHF" "CZK" "DKK" "EUR"
                                                                           "GBP" "HKD" "HUF" "ILS" "JPY" "MYR" "MXN"
                                                                           "NOK" "NZD" "PHP" "PLN" "SEK" "SGD" "THB"
                                                                           "TRY" "TWD" "USD"] }
                    { :code "PECUNIX" :name (terms/pecunix) :currency-types ["XAUG"] }])

(def payment-type-map (reduce #(assoc %1 (:code %2) %2) {} payment-types))

(defn payment-types-for-currency [currency-to-payment-type-map currency-code]
  (or (get currency-to-payment-type-map currency-code) #{}))

(defn add-payment-type-for-currency [currency-to-payment-type-map currency-code payment-type]
  (assoc currency-to-payment-type-map currency-code
    (conj (payment-types-for-currency currency-to-payment-type-map currency-code) payment-type)))

(defn add-payment-type-to-map [currency-to-payment-type-map payment-type]
  (reduce #(add-payment-type-for-currency %1 %2 payment-type) currency-to-payment-type-map
    (:currency-types payment-type)))

(defn create-currency-to-payment-type-map []
  (reduce #(add-payment-type-to-map %1 %2) {} payment-types))

(def currency-to-payment-type-map (create-currency-to-payment-type-map))

(defn get-payment [code]
  (get payment-type-map code))

(defn currencies-for [payment-type]
  (map currency/get-currency (:currency-types payment-type)))

(defn payment-type-str [payment-type]
  (:name payment-type))

(defn currency-and-payment-type-str [currency payment-type]
  (str (currency/currency-str currency) " by " (payment-type-str payment-type)))

(defn payment-types-for [currency-code]
  (get currency-to-payment-type-map currency-code))

; Simply allows the given currency to be displayed in a combobox or such.
(defrecord PaymentTypeDisplayAdaptor [payment-type]
  Object
  (toString [this] (payment-type-str (:payment-type this))))

(defn payment-type-adaptors []
  (map #(PaymentTypeDisplayAdaptor. %) payment-types))

(defn payment-type-adaptors-for [currency-code]
  (map #(PaymentTypeDisplayAdaptor. %) (payment-types-for currency-code)))