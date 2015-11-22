(ns pancetta.components.create
  (:require [reagent.core :as reagent :refer [atom]]
            [pancetta.common.ui :as ui]
            [matchbox.core :as m]))

(def cities ["London" "Paris" "Bruxelles"])
(def currencies {:gbp "£" :eur "€"})
(defonce ticket (atom {:from (cities 0)
                       :to (cities 1)
                       :on (-> (js/Date.) .toISOString (.slice 0 10))
                       :price {:currency :gbp}}))

(defn select-city [direction]
 [:select
   {:on-change #(swap! ticket assoc direction (-> % .-target .-value))
    :value (direction @ticket)
    :style {:font-size 20}}
   (for [city cities]
     [:option {:key city :value city} city])])

(defn submit [state]
  (let [user (:user @state)
        root (:root @state)
        tickets-ref (m/get-in root [:tickets user])]
    (m/conj! tickets-ref @ticket)))

(defn create-component [state]
  [:div {:class-name "rounded"
         :style {:background-color (:bg-widget ui/colors)
                 :padding "20px 40px"}}
    [:h2 "Enter the details of your ticket"]
    [:div "Go from: " (:from @ticket)
          " to: " (:to @ticket)
          " on: " (:on @ticket)
          " for: " (get-in @ticket [:price :amount])]
    [:form {:class-name "pure-form"}
      [:div [select-city :from]]
      [:div [select-city :to]]
      [:div
        [:input {:type "date"
                 :value (:on @ticket)
                 :on-change #(swap! ticket assoc :on (-> % .-target .-value))}]]
      [:div
        [:select {:value (get-in @ticket [:price :currency])
                  :on-change #(swap! ticket assoc-in [:price :currency] (-> % .-target .-value))}
          (for [[currency symbol] currencies]
            [:option {:key currency :value currency} symbol])]
        [:input {:type "number"
                 :value (get-in @ticket [:price :amount])
                 :on-change #(swap! ticket assoc-in [:price :amount] (-> % .-target .-value))
                 }]]]
     [:div [:input {:type "submit"
                    :class-name "pure-button"
                    :on-click #(submit state)
                    :value "Upload the damn ticket"}]]])
