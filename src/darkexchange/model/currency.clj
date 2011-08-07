(ns darkexchange.model.currency
  (:require [darkexchange.model.terms :as terms]))

(defn sort-currencies [currencies]
  (sort-by :name currencies))

(def currencies (sort-currencies
                  [ { :code "AED"       :name (terms/united-arab-emirates-dirhams) }
                    { :code "AFN"       :name (terms/afghanistan-afghanis) }
                    { :code "ALL"       :name (terms/albania-leke) }
                    { :code "AMD"       :name (terms/armenia-drams) }
                    { :code "ANG"       :name (terms/netherlands-antilles-guilders) }
                    { :code "AOA"       :name (terms/angola-kwanza) }
                    { :code "ARS"       :name (terms/argentina-pesos) }
                    { :code "AUD"       :name (terms/austrailian-dollar) }
                    { :code "AWG"       :name (terms/aruba-guilders) }
                    { :code "AZN"       :name (terms/azerbaijan-new-manats) }
                    { :code "BAM"       :name (terms/bosnia-and-herzegovina-convertible-marka) }
                    { :code "BBD"       :name (terms/barbados-dollars) }
                    { :code "BDT"       :name (terms/bangladesh-taka) }
                    { :code "BGN"       :name (terms/bulgaria-leva) }
                    { :code "BHD"       :name (terms/bahrain-dinars) }
                    { :code "BIF"       :name (terms/burundi-francs) }
                    { :code "BITCOIN"   :name (terms/bitcoin) }
                    { :code "BMD"       :name (terms/bermuda-dollars) }
                    { :code "BND"       :name (terms/brunei-darussalam-dollars) }
                    { :code "BOB"       :name (terms/bolivia-bolivianos) }
                    { :code "BRL"       :name (terms/brazil-real) }
                    { :code "BSD"       :name (terms/bahamas-dollars) }
                    { :code "BTN"       :name (terms/bhutan-ngultrum) }
                    { :code "BWP"       :name (terms/botswana-pulas) }
                    { :code "BYR"       :name (terms/belarus-rubles) }
                    { :code "BZD"       :name (terms/belize-dollars) }
                    { :code "CAD"       :name (terms/canadian-dollar) }
                    { :code "CDF"       :name (terms/congo-kinshasa-congolese-francs) }
                    { :code "CHF"       :name (terms/swiss-franc) }
                    { :code "CLP"       :name (terms/chile-pesos) }
                    { :code "CNY"       :name (terms/china-yuan-renminbi) }
                    { :code "COP"       :name (terms/colombia-pesos) }
                    { :code "CRC"       :name (terms/costa-rica-colones) }
                    { :code "CUP"       :name (terms/cuba-pesos) }
                    { :code "CVE"       :name (terms/cape-verde-escudos) }
                    { :code "CZK"       :name (terms/czech-republic-koruny) }
                    { :code "DJF"       :name (terms/djibouti-francs) }
                    { :code "DKK"       :name (terms/denmark-kroner) }
                    { :code "DOP"       :name (terms/dominican-republic-pesos) }
                    { :code "DZD"       :name (terms/algeria-dinars) }
                    { :code "EGP"       :name (terms/egypt-pounds) }
                    { :code "ERN"       :name (terms/eritrea-nakfa) }
                    { :code "ETB"       :name (terms/ethiopia-birr) }
                    { :code "EUR"       :name (terms/euro) }
                    { :code "FJD"       :name (terms/fiji-dollars) }
                    { :code "FKP"       :name (terms/falkland-islands-pounds) }
                    { :code "GBP"       :name (terms/great-british-pound) }
                    { :code "GEL"       :name (terms/georgia-lari) }
                    { :code "GGP"       :name (terms/guernsey-pounds) }
                    { :code "GHS"       :name (terms/ghana-cedis) }
                    { :code "GIP"       :name (terms/gibraltar-pounds) }
                    { :code "GMD"       :name (terms/gambia-dalasi) }
                    { :code "GNF"       :name (terms/guinea-francs) }
                    { :code "GTQ"       :name (terms/guatemala-quetzales) }
                    { :code "GYD"       :name (terms/guyana-dollars) }
                    { :code "HKD"       :name (terms/hong-kong-dollars) }
                    { :code "HNL"       :name (terms/honduras-lempiras) }
                    { :code "HRK"       :name (terms/croatia-kuna) }
                    { :code "HTG"       :name (terms/haiti-gourdes) }
                    { :code "HUF"       :name (terms/hungary-forint) }
                    { :code "IDR"       :name (terms/indonesia-rupiahs) }
                    { :code "ILS"       :name (terms/israel-new-shekels) }
                    { :code "IMP"       :name (terms/isle-of-man-pounds) }
                    { :code "INR"       :name (terms/india-rupees) }
                    { :code "IQD"       :name (terms/iraq-dinars) }
                    { :code "IRR"       :name (terms/iran-rials) }
                    { :code "ISK"       :name (terms/iceland-kronur) }
                    { :code "JEP"       :name (terms/jersey-pounds) }
                    { :code "JMD"       :name (terms/jamaica-dollars) }
                    { :code "JOD"       :name (terms/jordan-dinars) }
                    { :code "JPY"       :name (terms/japan-yen) }
                    { :code "KES"       :name (terms/kenya-shillings) }
                    { :code "KGS"       :name (terms/kyrgyzstan-soms) }
                    { :code "KHR"       :name (terms/cambodia-riels) }
                    { :code "KMF"       :name (terms/comoros-francs) }
                    { :code "KPW"       :name (terms/korea-north-won) }
                    { :code "KRW"       :name (terms/korea-south-won) }
                    { :code "KWD"       :name (terms/kuwait-dinars) }
                    { :code "KYD"       :name (terms/cayman-islands-dollars) }
                    { :code "KZT"       :name (terms/kazakhstan-tenge) }
                    { :code "LAK"       :name (terms/laos-kips) }
                    { :code "LBP"       :name (terms/lebanon-pounds) }
                    { :code "LKR"       :name (terms/sri-lanka-rupees) }
                    { :code "LRD"       :name (terms/liberia-dollars) }
                    { :code "LSL"       :name (terms/lesotho-maloti) }
                    { :code "LTL"       :name (terms/lithuania-litai) }
                    { :code "LVL"       :name (terms/latvia-lati) }
                    { :code "LYD"       :name (terms/libya-dinars) }
                    { :code "MAD"       :name (terms/morocco-dirhams) }
                    { :code "MDL"       :name (terms/moldova-lei) }
                    { :code "MGA"       :name (terms/madagascar-ariary) }
                    { :code "MKD"       :name (terms/macedonia-denars) }
                    { :code "MMK"       :name (terms/myanmar-kyats) }
                    { :code "MNT"       :name (terms/mongolia-tugriks) }
                    { :code "MOP"       :name (terms/macau-patacas) }
                    { :code "MRO"       :name (terms/mauritania-ouguiyas) }
                    { :code "MUR"       :name (terms/mauritius-rupees) }
                    { :code "MVR"       :name (terms/maldives-rufiyaa) }
                    { :code "MWK"       :name (terms/malawi-kwachas) }
                    { :code "MXN"       :name (terms/mexico-pesos) }
                    { :code "MYR"       :name (terms/malaysia-ringgits) }
                    { :code "MZN"       :name (terms/mozambique-meticais) }
                    { :code "NAD"       :name (terms/namibia-dollars) }
                    { :code "NAMECOIN"  :name (terms/namecoin) }
                    { :code "NGN"       :name (terms/nigeria-nairas) }
                    { :code "NIO"       :name (terms/nicaragua-cordobas) }
                    { :code "NOK"       :name (terms/norway-krone) }
                    { :code "NPR"       :name (terms/nepal-rupees) }
                    { :code "NZD"       :name (terms/new-zealand-dollars) }
                    { :code "OMR"       :name (terms/oman-rials) }
                    { :code "PAB"       :name (terms/panama-balboa) }
                    { :code "PEN"       :name (terms/peru-nuevos-soles) }
                    { :code "PGK"       :name (terms/papua-new-guinea-kina) }
                    { :code "PHP"       :name (terms/philippines-pesos) }
                    { :code "PKR"       :name (terms/pakistan-rupees) }
                    { :code "PLN"       :name (terms/poland-zlotych) }
                    { :code "PYG"       :name (terms/paraguay-guarani) }
                    { :code "QAR"       :name (terms/qatar-rials) }
                    { :code "RON"       :name (terms/romania-new-lei) }
                    { :code "RSD"       :name (terms/serbia-dinars) }
                    { :code "RUB"       :name (terms/russia-rubles) }
                    { :code "RWF"       :name (terms/rwanda-francs) }
                    { :code "SAR"       :name (terms/saudi-arabia-riyals) }
                    { :code "SBD"       :name (terms/solomon-islands-dollars) }
                    { :code "SCR"       :name (terms/seychelles-rupees) }
                    { :code "SDG"       :name (terms/sudan-pounds) }
                    { :code "SEK"       :name (terms/sweden-kronor) }
                    { :code "SGD"       :name (terms/singapore-dollars) }
                    { :code "SHP"       :name (terms/saint-helena-pounds) }
                    { :code "SLL"       :name (terms/sierra-leone-leones) }
                    { :code "SOS"       :name (terms/somalia-shillings) }
                    { :code "SPL"       :name (terms/seborga-luigini) }
                    { :code "SRD"       :name (terms/suriname-dollars) }
                    { :code "STD"       :name (terms/sao-tome-and-principe-dobras) }
                    { :code "SVC"       :name (terms/el-salvador-colones) }
                    { :code "SYP"       :name (terms/syria-pounds) }
                    { :code "SZL"       :name (terms/swaziland-emalangeni) }
                    { :code "THB"       :name (terms/thailand-baht) }
                    { :code "TJS"       :name (terms/tajikistan-somoni) }
                    { :code "TMM"       :name (terms/turkmenistan-manats) }
                    { :code "TND"       :name (terms/tunisia-dinars) }
                    { :code "TOP"       :name (terms/tonga-paanga) }
                    { :code "TRY"       :name (terms/turkey-new-lira) }
                    { :code "TTD"       :name (terms/trinidad-and-tobago-dollars) }
                    { :code "TVD"       :name (terms/tuvalu-dollars) }
                    { :code "TWD"       :name (terms/taiwan-new-dollars) }
                    { :code "TZS"       :name (terms/tanzania-shillings) }
                    { :code "UAH"       :name (terms/ukraine-hryvnia) }
                    { :code "UGX"       :name (terms/uganda-shillings) }
                    { :code "USD"       :name (terms/us-dollars) }
                    { :code "UYU"       :name (terms/uruguay-pesos) }
                    { :code "UZS"       :name (terms/uzbekistan-sums) }
                    { :code "VEF"       :name (terms/venezuela-bolivares-fuertes) }
                    { :code "VND"       :name (terms/viet-nam-dong) }
                    { :code "VUV"       :name (terms/vanuatu-vatu) }
                    { :code "WST"       :name (terms/samoa-tala) }
                    { :code "XAF"       :name (terms/communaute-financiere-africaine-beac-francs) }
                    { :code "XAG"       :name (terms/silver-ounces) }
                    { :code "XAU"       :name (terms/gold-ounces) }
                    { :code "XAUG"      :name (terms/gold-grams) }
                    { :code "XCD"       :name (terms/east-caribbean-dollars) }
                    { :code "XOF"       :name (terms/communaute-financiere-africaine-bceao-francs) }
                    { :code "XPD"       :name (terms/palladium-ounces) }
                    { :code "XPF"       :name (terms/comptoirs-francais-du-pacifique-francs) }
                    { :code "XPT"       :name (terms/platinum-ounces) }
                    { :code "YER"       :name (terms/yemen-rials) }
                    { :code "ZAR"       :name (terms/south-africa-rand) }
                    { :code "ZMK"       :name (terms/zambia-kwacha) }
                    { :code "ZWD"       :name (terms/zimbabwe-dollars) } ]))

(def currency-map (reduce #(assoc %1 (:code %2) %2) {} currencies ))

(defn get-currency [code]
  (get currency-map code))

(defn currency-str [currency]
  (:name currency))

; Simply allows the given currency to be displayed in a combobox or such.
(defrecord CurrencyDisplayAdaptor [currency]
  Object
  (toString [this] (currency-str (:currency this))))

(defn currency-adaptors []
  (map #(CurrencyDisplayAdaptor. %) currencies))