(ns pancetta.components.navbar
  (:require [secretary.core :as secretary :include-macros true]
            [pancetta.common.ui :as ui]
            [reagent.core :as reagent :refer [atom]]))

(defonce style {:header-container {:background-color (:grey-dark ui/colors)}

                :container {:height 60 :display "flex"
                            :color (:grey-light ui/colors)
                            :align-items "stretch"
                            :flex-direction "row"
                            :flex-wrap "wrap"
                            :justify-content "flex-start"
                            :max-width (:viewport-max ui/layout)
                            :min-width (:viewport-min ui/layout)
                            :margin-left "auto"
                            :margin-right "auto"}

                :item {:padding 15 :cursor "pointer"}

                :item-hovered {:background-color (:grey ui/colors)}

                :item-right {:margin-left "auto"}})

(defn logout [state] (swap! state assoc :user nil))

(defonce items [{:label "Home" :action #(secretary/dispatch! "/")}
                {:label "Create" :action #(secretary/dispatch! "/create")}
                {:label "Logout" :action logout :right true}])

(defn navbar-component [state]
  (let [hovered (atom nil)]
    (fn []
      [:div {:style (:header-container style)}
        [:div {:style (:container style)}
          (doall (map-indexed
            (fn [idx item]
              [:div {:style (merge (:item style)
                                    (if (:right item) (:item-right style))
                                    (if (= @hovered idx) (:item-hovered style)))
                      :on-click #((:action item) state)
                      :on-mouse-over #(reset! hovered idx)
                      :on-mouse-out #(reset! hovered nil)
                      :key idx} (:label item)]) items))]])))
