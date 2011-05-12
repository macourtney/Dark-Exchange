(ns darkexchange.model.terms
  (:import [java.util ResourceBundle Locale])
  (:require [clojure.contrib.logging :as logging]
            [clojure.tools.html-utils :as html-utils] 
            [clojure.tools.loading-utils :as loading-utils]
            [clojure.tools.string-utils :as string-utils]))

(defn
  parameterize [term params]
  (let [escaped-term (html-utils/xml-encode term)]
    (string-utils/str-replace-if
      (if (seq params)
        (reduce 
          string-utils/str-replace-pair
          escaped-term
          (map #(list (str "{" %1 "}") (str %2)) (iterate inc 0) params))
        escaped-term)
      { "{{}" "{", "{}}" "}"})))

(let [term-bundle (. ResourceBundle getBundle "Terms" (. Locale getDefault))]
  (doseq [property-key (.keySet term-bundle)]
    (eval 
      (list 
        'defn 
        (symbol (loading-utils/underscores-to-dashes (.toLowerCase property-key))) 
        ['& 'params]
        (list 'parameterize (.getString term-bundle property-key) 'params)))))