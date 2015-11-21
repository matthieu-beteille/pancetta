(ns pancetta.components.navbar
  (:require [secretary.core :as secretary :include-macros true]
            [pancetta.common.ui :as ui]))

(defonce style {:header-container {:background-color (:grey-dark ui/colors)}

                :container {:height 60 :display "flex"
                            :color (:grey-light ui/colors)
                            :align-items "center"
                            :flex-direction "row"
                            :flex-wrap "wrap"
                            :justify-content "flex-start"
                            :max-width (:viewport-max ui/layout)
                            :min-width (:viewport-min ui/layout)
                            :margin-left "auto"
                            :margin-right "auto"}

                :item {:margin 10 :cursor "pointer"}

                :logout {:margin-left "auto"}})

(defn logout [state]
  (swap! state assoc :user nil))

(defn navbar-component [state]
  [:div {:style (:header-container style)}
    [:div {:style (:container style)}
      [:div {:style (:item style) :on-click #(secretary/dispatch! "/")} "Home"]
      [:div {:style (:item style) :on-click #(secretary/dispatch! "/create")} "Create"]
      [:div {:style (merge (:item style) (:logout style)) :on-click #(logout state)} "Logout"]]])
