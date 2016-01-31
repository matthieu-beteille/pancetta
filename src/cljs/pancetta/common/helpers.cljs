(ns pancetta.common.helpers
  (:require [pancetta.domain :refer [currencies]]))

(defn price-to-str [price]
  (str (get-in price [:currency :symbol])
       (:amount price)))