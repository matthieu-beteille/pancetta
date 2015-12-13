(ns pancetta.common.helpers)

(defn price-to-str [price]
  (let [currency {:gbp "£" :eur "€"}]
    (str ((keyword (:currency price)) currency)
         (:amount price))))
