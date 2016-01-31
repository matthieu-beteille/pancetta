(ns pancetta.domain
  (:require [schema.core :as s
             :include-macros true]))

(def cities ["London" "Paris" "Bruxelles"])

(s/defrecord Currency [code :- s/Str
                       symbol :- s/Str
                       ])

(def currencies {:gbp (->Currency "GBP" "£")
                 :eur (->Currency "EUR" "€")})

(s/defrecord Price [amount :- s/Num
                    currency :- Currency
                  ])

(s/defrecord User [uid :- s/Str])

(s/defrecord Ticket [from
                     to
                     on
                     at
                     price :- Price
                     owner :- s/Str
                     ])

(defn make-default-ticket []
  (->Ticket
    (cities 0)
    (cities 1)
    (-> (js/Date.) .toISOString (.slice 0 10))
    "07:42"
    (->Price 35 (:gbp currencies))
    ""))
