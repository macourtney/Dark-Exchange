(ns test.fixtures.peer
  (:use darkexchange.database.util)
  (:require [test.init :as test-init])
  (:import [java.util Date]))

(def records [
  { :id 1
    :destination "LlC5T8BJovJ2TONm1NuJ4KdmwhFeSRtajxncTi3YvAQeRIvMUqq7IcSTAf5HZiAsKvprZTZa1SncxiCcNivxbQgHZ0sy~AkDOpURrN3BRdQqQn2b8qhYWgs~xvt-Yn7ECrXSgpR7AKjhoFW6~AtiXGSxTdbQafmlZnuwivnzJIb29BUsUx0nOBmcG918nQtethnxnmnTKqLqFBc5c2qP6evP2xYrvWwGaTM4QPidzq-aqEoWUkc1rdkozqWd~M2A0WhNGAjB432Jpp9N8KCacE6SEPM~uKOSsvQtPPZk~9V3UYnDU0941HhhHZgaHZpIy7yeDKkZCGqUMTMh1yEPYwqpOfHbFraoldALDugKz~NkJ0QVL~jxCh40xxnBTBhLsCJuzTe~FfL4odl1vtmwVlACMhaNBHqOaBgKGqUssqmfC1TdLkswnSOni7luA8RZHVgmRI0MnzlHHwg9lHdY53w7Nok1X404OzaWCNy75-bP9po-1DTax4IBNFDpvHrcAAAA"
    :created_at (new Date)
    :updated_at (new Date)
    :notified nil }
  { :id 2
    :destination "test destination"
    :created_at (new Date)
    :updated_at (new Date)
    :notified nil }])

(def fixture-table-name :peers)

(defn fixture [function]
  (try
    (apply insert-into fixture-table-name records)
    (function)
    (finally 
      (delete fixture-table-name ["true"]))))