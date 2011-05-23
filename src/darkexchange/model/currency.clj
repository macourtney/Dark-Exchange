(ns darkexchange.model.currency)

(def currencies [{ :code "USD" :name "US Dollar" }
                  { :code "CAD" :name "Canadian Dollar" }
                  { :code "EUR" :name "Euro" }
                  { :code "GBP" :name "Great British Pound" }
                  { :code "AUD" :name "Austrailian Dollar" }
                  { :code "CHF" :name "Swis Franc" }])

(def payment-types [{ :code "CAM" :name "Cash in Mail" :currency-types :all }
                     { :code "CHM" :name "Check or Money Order in Mail" :currency-types :all }
                     { :code "DWOLLA" :name "Dwolla" :currency-types ["USD"] }])

(defn currencies-to-map []
  (reduce currencies {} #(assoc %1 (:code %2) %2)))

(def currency-map (currencies-to-map))

(def find-currency [code]
  (get currency-map code))

(defn currencies-for [payment-type]
  (map find-currency (:currency-types payment-type)))

(defn currency-str [currency]
  (:name currency))

(defn payment-type-str [payment-type]
  (:name payment-type))

(defn currency-and-payment-type-str [currency payment-type]
  (str (currency-str currency) " by " (payment-type-str payment-type)))