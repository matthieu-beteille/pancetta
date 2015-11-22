(ns pancetta.components.tickets
  (:require [matchbox.core :as m]
            [pancetta.common.ui :as ui]
            [reagent.core :as reagent :refer [atom]]))

(def style {:item {:background-color (:bg-widget ui/colors)
                   :border-radius 3
                   :margin-bottom 20
                   :padding 20}
            :cross {:float "right"
                    :cursor "pointer"
                    :font-weight "bold"
                    :color (:error ui/colors)}})

(defn ticket-component [user-root [id ticket]]
  [:div {:style (:item style)}
    [:span "Ticket from " (:from ticket)]
    [:span " to " (:to ticket)]
    [:span " on " (:on ticket)]
    [:span " for " (get-in ticket [:price :amount])]
    [:span {:style (:cross style)
            :on-click #(m/remove! (m/get-in user-root id))} "Delete"]])

(defn tickets-component [state]
  (let [child (m/get-in (:root @state) [:tickets (:user @state)])
        tickets (atom nil)]
    (m/listen-to child :value #(reset! tickets (-> (get % 1) reverse)))
    (fn []
      (if (nil? @tickets)
        [:div "You have no tickets"]
        [:div (map #(-> [:div {:key (get % 0)} [ticket-component child %]]) @tickets)]))))
