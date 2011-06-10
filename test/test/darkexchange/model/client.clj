(ns test.darkexchange.model.client
  (:require [test.init :as init] 
            [darkexchange.model.user :as user-model]
            [test.darkexchange.util :as test-util]
            [test.fixtures.user :as user-fixture]
            [test.fixtures.util :as fixtures-util]) 
  (:use clojure.contrib.test-is
        darkexchange.model.client))

(fixtures-util/use-fixture-maps :once user-fixture/fixture-map)

(defn create-user-map []
  (let [user (user-model/current-user)]
    { :name (:name user) :public-key (:public_key user) :public-key-algorithm (:public_key_algorithm user) })) 

(deftest test-create-request-map
  (test-util/login)
  (let [action :test
        data "test"
        destination nil
        request-map (create-request-map destination action data)]
    (is request-map "The request map could not be created.")
    (is (= action (:action request-map)) "The wrong action was added to the request map.")
    (is (= data (:data request-map)) "The wrong data was added to the request map.")
    (is (= { :destination nil :user (create-user-map) } (:from request-map))
      "The wrong from data was added to the request map."))
  (test-util/logout))