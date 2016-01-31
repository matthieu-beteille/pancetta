(ns pancetta.components.create
  (:require [pancetta.domain :as p :refer [cities currencies]]
            [reagent.core :as reagent :refer [atom]]
            [schema.core :as s :include-macros true]
            [secretary.core :as secretary]
            [pancetta.common.ui :as ui]
            [matchbox.core :as m]))

;(def cities ["London" "Paris" "Bruxelles"])
;(def currencies {:gbp "£" :eur "€"})
;(defonce ticket (atom {:from (cities 0)
;                       :to (cities 1)
;                       :on (-> (js/Date.) .toISOString (.slice 0 10))
;                       :at "07:42"
;                       :price {:currency :gbp :amount 35}}))

(defonce ticket (atom (p/make-default-ticket)))

(defn city-selector [direction]
 [:select
   {:on-change #(swap! ticket assoc direction (-> % .-target .-value))
    :value (direction @ticket)}
   (for [city cities]
     [:option {:key city :value city} city])])

(defn date-picker []
  [:input {:type "date"
           :min (-> (js/Date.) .toISOString (.slice 0 10))
           :value (:on @ticket)
           :on-change #(swap! ticket assoc :on (-> % .-target .-value))}])

(defn time-input []
  [:input {:type "text"
           :class-name "pure-input-1-4"
           :style {:text-align "center"}
           :value (:at @ticket)
           :on-change #(swap! ticket assoc :at (-> % .-target .-value))}])

(defn price-form []
  [:span
    [:select
      {:value
        ((clojure.set/map-invert currencies) (get-in @ticket [:price :currency]))
       :on-change
        #(swap! ticket assoc-in
                [:price :currency]
                (get
                  currencies
                  (keyword (-> % .-target .-value))))}
      (for [[k  {:keys [symbol]}] currencies]
        [:option {:key k :value k} symbol])]
    [:input {:type "number"
             :class-name "pure-input-1-4"
             :value (get-in @ticket [:price :amount])
             :on-change #(swap! ticket assoc-in [:price :amount] (-> % .-target .-value  (js/parseInt 10)))
             }]])

(defn submit [state]
  (let [user (:user @state)
        root (:root @state)
        tickets-ref (m/get-in root [:tickets])]
    (swap! ticket assoc :owner user)
    (s/validate p/Ticket @ticket)
    (m/conj! tickets-ref @ticket)
    (secretary/dispatch! "/tickets")))

(defn create-component [state]
  [:div {:class-name "rounded"
         :style {:background-color (:bg-widget ui/colors)
                 :padding "20px 40px"}}
    [:h2 "Enter the details of your ticket"]
    [:form {:class-name "pure-form"
            :style {:font-size 20
                    :line-height 4}}
      [:div
        "This is a train from "
        [city-selector :from]
        " to "
        [city-selector :to]
        [:br] "On the "
        [date-picker]
        " at "
        [time-input]
        [:br] "For the lovely price of "
        [price-form]
        [:br]
        [:input {:type "button"
                 :disabled (<= (get-in @ticket [:price :amount]) 0)
                 :style {:margin-top 30}
                 :class-name "button-primary pure-button"
                 :on-click #(submit state)
                 :value "Upload the damn ticket"}]]]])
