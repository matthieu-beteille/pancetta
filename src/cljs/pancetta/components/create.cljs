(ns pancetta.components.create
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary]
            [pancetta.common.ui :as ui]
            [matchbox.core :as m]))

(def cities ["London" "Paris" "Bruxelles"])
(def currencies {:gbp "£" :eur "€"})
(defonce ticket (atom {:from (cities 0)
                       :to (cities 1)
                       :on (-> (js/Date.) .toISOString (.slice 0 10))
                       :price {:currency :gbp}}))

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

(defn price-form []
  [:span
    [:select
      {:value
        (get-in @ticket [:price :currency])
       :on-change
        #(swap! ticket assoc-in [:price :currency] (-> % .-target .-value))}
      (for [[currency symbol] currencies]
        [:option {:key currency :value currency} symbol])]
    [:input {:type "number"
             :class-name "pure-input-1-4"
             :value (get-in @ticket [:price :amount])
             :on-change #(swap! ticket assoc-in [:price :amount] (-> % .-target .-value))
             }]])

(defn submit [state]
  (let [user (:user @state)
        root (:root @state)
        tickets-ref (m/get-in root [:tickets])]
    (swap! ticket assoc :owner user)
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
        [:br] "For the lovely price of "
        [price-form]
        [:br]
        [:input {:type "button"
                 :disabled (<= (get-in @ticket [:price :amount]) 0)
                 :style {:margin-top 30}
                 :class-name "button-primary pure-button"
                 :on-click #(submit state)
                 :value "Upload the damn ticket"}]]]])
