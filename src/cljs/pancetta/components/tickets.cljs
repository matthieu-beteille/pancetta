(ns pancetta.components.tickets
  (:require [matchbox.core :as m]
            [secretary.core :as secretary :include-macros true]
            [pancetta.common.ui :as ui]
            [pancetta.common.helpers :as h :refer [price-to-str]]
            [reagent.core :as reagent :refer [atom]]))

(def style {:item {:background-color (:bg-widget ui/colors)
                   :border-radius 3
                   :margin-bottom 2
                   :padding 20}
            :share-box {:background-color (:bg-widget ui/colors)
                        :border-radius 3
                        :margin-bottom 2
                        :padding 20
                        :display "flex"
                        :justify-content "space-between"}
            :share-link {:flex-grow 1 :margin-left 20}
            :share-btn {:float "right"
                        :margin-right 20
                        :font-weight "bold"
                        :cursor "pointer"}
            :go-btn {:margin-left 10
                     :font-weight "bold"
                     :cursor "pointer"}
            :delete-btn {:float "right"
                         :cursor "pointer"
                         :font-weight "bold"
                         :color (:error ui/colors)}})

(defn ticket-component [state id ticket]
  (let [expanded (atom false)]
    (fn []
      [:div
        [:div {:style (:item style)}
          [:span "Ticket from " (:from ticket)]
          [:span " to " (:to ticket)]
          [:span " on " (:on ticket)]
          [:span " for " (price-to-str (:price ticket))]
          [:span {:style (:delete-btn style)
                  :on-click #(m/remove! (m/get-in (:root @state) [:tickets id]))} "Delete"]
          [:span {:style (:share-btn style)
                  :on-click (fn [] (swap! expanded #(not %)))} "Share"]]
        (if @expanded
          (let [share-url (str "/ticket/" (name id))]
            [:div {:style (:share-box style)}
              [:span "URL: "]
              [:input {:type "text" :style (:share-link style)
                       :value (str js/window.location.protocol
                                   "//"
                                   js/window.location.hostname
                                   (when (not= js/window.location.port "")
                                     (str ":" js/window.location.port))
                                   "/#" share-url)}]
              [:span {:style (:go-btn style) :on-click #(secretary/dispatch! share-url)} "GO"]]))])))

(defn tickets-component [state]
  (let [child (->
                (m/get-in (:root @state) :tickets)
                (.orderByChild "owner")
                (.equalTo (:user @state)))
        tickets (atom nil)]
    (m/listen-to child :value #(reset! tickets (-> (get % 1) reverse)))
    (fn []
      (if (nil? @tickets)
        [:div "You have no tickets"]
        [:div
          [:h2 "My tickets"]
          (for [[id ticket] @tickets]
            [:div {:key id} [ticket-component state id ticket]])]))))
