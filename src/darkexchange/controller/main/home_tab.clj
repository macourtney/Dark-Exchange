(ns darkexchange.controller.main.home-tab
  (:require [clojure.contrib.logging :as logging]
            [darkexchange.controller.main.home.open-offer-panel :as open-offer-panel]
            [darkexchange.controller.main.home.open-trade-panel :as open-trade-panel]))

(defn load-data [main-frame]
  (open-trade-panel/load-data (open-offer-panel/load-data main-frame)))

(defn attach [main-frame]
  (open-trade-panel/attach (open-offer-panel/attach main-frame)))

(defn init [main-frame]
  (attach (load-data main-frame)))