(ns darkexchange.model.util)

(defn run-interceptors [interceptors arg]
  (if (empty? interceptors)
    arg
    ((apply comp interceptors) arg)))