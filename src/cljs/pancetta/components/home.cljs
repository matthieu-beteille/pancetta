(ns pancetta.components.home
  (:require [matchbox.core :as m]
            [pancetta.common.helpers :as h :refer [price-to-str]]
            [secretary.core :as secretary]
            [reagent.core :as reagent :refer [atom]]))

(def style {:form-container {:background-color "white"
                             :padding "20px 30px"
                             :margin 5}
            :results-container {:margin 5}
            :ticket {:background-color "white"
                     :margin 1
                     :border-radius 3
                     :padding 10
                     :cursor "pointer"}
            :form-group {:margin "20px 0px"}})

(defonce query (atom {:from "London"
                  :to "Paris"
                  :on (-> (js/Date.) .toISOString (.slice 0 10))}))

(defonce tickets (atom nil))

;; should be retrieved from firebase
(def cities ["Paris", "London", "Bruxelles"])

(defn get-tickets [root]
  (let [tickets-ref (->
                     (m/get-in root [:tickets])
                     (.orderByChild "on")
                     (.equalTo (:on @query)))]
    (m/listen-to tickets-ref
                 :value
                 (fn [[_ res]]
                   (reset! tickets (for [[_ {:keys [to from]} :as ticket] res
                                         :when (and (= (:to @query) to)
                                                    (= (:from @query) from))]
                                     ticket))))))

(defn ticket-component [id ticket]
  [:div {:style (:ticket style)
         :on-click #(secretary/dispatch! (str "ticket/" (name id)))}
   [:span (str "Ticket from " (:from ticket))]
   [:span (str " to " (:to ticket))]
   [:span (str " for " (price-to-str (:price ticket)))]])

(defn form-component [state]
  [:div {:class-name "pure-u-3-5"}
    [:form {:style (:form-container style) :class-name "pure-form"}
     [:h2 "Find a ticket"]
     [:div {:style (:form-group style)}
      [:span "I am looking for a train on the "]
      [:input {:type "date"
               :min (-> (js/Date.) .toISOString (.slice 0 10))
               :value (:on @query)
               :on-change (fn [e] (swap! query
                                         assoc
                                         :on (-> e .-target .-value)))}]]
     [:div {:style (:form-group style)}
      [:span "from "]
      [:select {:on-change #(swap! query assoc :from (-> % .-target .-value))
                :default-value "London"}
       (for [city cities]
         [:option {:key city} city])]
      [:span " to "]
      [:select  {:on-change #(swap! query assoc :to (-> % .-target .-value))
                 :default-value "Paris"}
       (for [city (doall (filter #(not= % (:from @query)) cities))]
         [:option {:key city} city])]]
     [:a {:on-click #(get-tickets (:root @state))
          :class-name "button-primary pure-button"} "Search"]]])

(defn results-component [state]
  [:div {:class-name "pure-u-2-5"}
   [:div {:style (:results-container style)}
    (if (nil? @tickets)
      [:div]
      (if (empty? @tickets)
        [:h2 "No ticket available"]
        (for [[ticket-id ticket] @tickets]
          [:div {:key ticket-id}
           [ticket-component ticket-id ticket]])))]])

(defn home-component [state]
  [:div
   [form-component state]
   [results-component state]])
