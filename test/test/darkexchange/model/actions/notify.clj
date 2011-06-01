(ns test.darkexchange.model.actions.notify
  (:require [darkexchange.model.actions.action-keys :as action-keys]
            [darkexchange.model.peer :as peer-model]
            [test.fixtures.peer :as peer-fixture]
            [test.fixtures.util :as fixtures-util]) 
  (:use clojure.test
        darkexchange.model.actions.notify))

(fixtures-util/use-fixture-maps :once peer-fixture/fixture-map)

(deftest test-action
  (let [test-destination "E2gmNv6IjYPmSlosy10GZ50T634nPc5M8ZHgJ6DNhg262GECdwgQXE4GkWDqB6Q6yYib9l88HlyWQVM5qaWIh71XqnMCFwq2yzO5pCnzaRVGYSxM7rdV-dChvNEfVLrDajd~sbz~OYinY8NB26-NItSPHw0gKqdDAfO7OuAd6Es57RROi4W8hU~tg4m9Z0Xr-a76mB9uqVynMRAUt~AhyD4OJ5uo6WL5wa3D6XJ2cuIC4rqT87h7ayk3ZOEhewc9L~aUyXtqC2UzitjV1BnONL6zpPQ0dwS-Snuak0C~NBaFNf6ooZF19vPSuXcnB6t6~ezy3P2IqBxdzrJuN1MxNHIHOmGtpsxgjZZkZvBOdrjm-II9~9D7AnG-LwZ1RkPge6bTnyoqex1rEm~~jywxwirEjcl6TOuJ7JIv2iOaU8imU6CpRwrxCGB1yjA1pcGFv841AvdUo7-Tpy1wtyIBi-n~5iNq6PNGwhUBYsQKmBBSolHrW72gCqeOmg88XyTaAAAA"]
    (is (nil? (peer-model/find-peer test-destination)) "Destination already exists in the database.")
    (is (= { :data "ok" } (action { :data { :destination test-destination } })) "Notify action failed.")
    (is (peer-model/find-peer test-destination) "The destination was not added to the database")
    (is (= { :data "ok" } (action { :data { :destination test-destination } })) "Notify action failed.")
    (let [peer (peer-model/find-peer test-destination)]
      (is peer "The destination was not updated in the database")
      (peer-model/destroy-record peer)
      (is (nil? (peer-model/find-record { :id (:id peer) })) "Clean up failed."))))

(deftest test-action-key
  (is (= action-key action-keys/notify-action-key)))