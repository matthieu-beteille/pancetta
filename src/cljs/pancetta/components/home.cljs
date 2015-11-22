(ns pancetta.components.home
  (:require [matchbox.core :as m]
            [reagent.core :as reagent :refer [atom]]))

(defn home-component [state]
  (let [tickets-ref (->
                      (m/get-in (:root @state) [:tickets])
                      (.orderByChild "price/amount")
                      (.limitToLast 10))
        tickets (atom nil)]
    (m/listen-to tickets-ref :value #(reset! tickets (get % 1)))
    (fn []
      [:div
        [:h1 {:on-click #(.log js/console "click")} "HOME PAGE"]
        [:div
          (for [[ticket-id ticket] @tickets]
            [:div {:key ticket-id}
              (:from ticket)
              " -> "
              (:to ticket)
              " on: " (:on ticket)
              " for "
              (get-in ticket [:price :amount])])]])))
