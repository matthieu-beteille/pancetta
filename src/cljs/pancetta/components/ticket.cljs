(ns pancetta.components.ticket
  (:require [matchbox.core :as m]
            [reagent.core :as reagent :refer [atom]]
            [pancetta.components.login :as login]
            [pancetta.common.ui :as ui]))

(def style {:ticket {:background-color (:bg-widget ui/colors)
                     :border-radius 3
                     :display "flex"
                     :align-items "center"
                     :padding 20}
            :btn {:margin-left "auto"}})

(defn ticket-component [ticket-id state]
  (let [child (m/get-in (:root @state) [:tickets ticket-id])
        ticket (atom nil)]
    (m/listen-to child :value #(reset! ticket (get % 1)))
    (fn []
      (let [is-logged-in (not (nil? (:user @state)))]
        (if (nil? @ticket)
          [:div "Loading"]
          [:div
            [:h2 "Ticket"]
            [:div {:style (:ticket style)}
              [:span (str "From " (:from @ticket))]
              [:span (str " to " (:to @ticket))]
              [:span (str " on " (:on @ticket))]
              [:span (str " for " (get-in @ticket [:price :amount]))]
              (if-not is-logged-in
                [:button {:style (:btn style)
                          :class-name "button-primary pure-button"
                          :on-click #(login/login! state)} "Login with FB"]
                [:button {:style (:btn style)
                          :class-name "button-primary pure-button"
                          :on-click #(login/login! state)} "Buy"])]])))))
