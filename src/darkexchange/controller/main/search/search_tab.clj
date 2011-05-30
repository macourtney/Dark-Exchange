(ns darkexchange.controller.main.search.search-tab
  (:require [darkexchange.controller.offer.has-panel :as offer-has-panel]
            [darkexchange.controller.offer.wants-panel :as offer-wants-panel]))

(defn attach [main-frame]
  (offer-has-panel/load-data main-frame)
  (offer-wants-panel/load-data main-frame))