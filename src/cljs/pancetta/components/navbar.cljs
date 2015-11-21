(ns pancetta.components.navbar
  (:require [secretary.core :as secretary :include-macros true]))

(defonce style {:container {:height 60 :background-color "#30323A"
                            :display "flex" :color "#909297"
                            :align-items "center" :flex-direction "row"
                            :flex-wrap "wrap" :justify-content "flex-start"}
                :item {:margin 10 :cursor "pointer"}})

;; logout function
(defn logout [state]
  (swap! state assoc :user nil))

; (secretary/dispatch! "/users/gf3")

(defn navbar-component [state]
  [:div {:style (:container style)}
    [:div {:style (:item style) :on-click #(secretary/dispatch! "/")} "Home"]
    [:div {:style (:item style) :on-click #(secretary/dispatch! "/create")} "Create"]
    [:div {:style (:item style) :on-click #(logout state)} "Logout"]])
