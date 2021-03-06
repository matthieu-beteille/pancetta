(ns pancetta.components.navbar
  (:require [secretary.core :as secretary :include-macros true]
            [pancetta.common.ui :as ui]
            [pancetta.components.login :as login]
            [reagent.session :as session]
            [reagent.core :as reagent :refer [atom]]))

(def style {:header-container {:background-color (:bg-negative ui/colors)}

            :container {:height 60 :display "flex"
                        :color (:text-negative ui/colors)
                        :align-items "stretch"
                        :flex-direction "row"
                        :flex-wrap "wrap"
                        :justify-content "flex-start"
                        :max-width (:viewport-max ui/layout)
                        :min-width (:viewport-min ui/layout)
                        :margin-left "auto"
                        :margin-right "auto"}

            :item {:padding 15 :cursor "pointer"}

            :item-hovered {:background-color (:bg-negative-active ui/colors)
                           :color (:text-negative-active ui/colors)}

            :item-active {:background-color (:bg ui/colors) :color (:bg-negative ui/colors)}

            :item-right {:margin-left "auto"}})

(defn logout! [state] (swap! state assoc :user nil))

(def items [{:label "Home" :action #(secretary/dispatch! "/") :page-name "home"}
            {:label "Create" :action #(secretary/dispatch! "/create") :page-name "create" :private true}
            {:label "Tickets" :action #(secretary/dispatch! "/tickets") :page-name "tickets" :private true}])

(defn navbar-component [state]
  (let [hovered (atom nil)]
    (fn []
      (let [current-page (session/get :current-page)
            is-logged-in (not (nil? (:user @state)))
            filtered-items (conj (into [] (filter #(or (not (:private %)) is-logged-in) items))
                                  (if is-logged-in {:label "Logout" :action logout! :right true}
                                                    {:label "Login" :action login/login! :right true}))]
        [:div {:style (:header-container style)}
          [:div {:style (:container style)}
            (doall (map-indexed
              (fn [idx item]
                [:div {:style (merge (:item style)
                                      (if (:right item) (:item-right style))
                                      (if (= @hovered idx) (:item-hovered style))
                                      (if (= (:name current-page) (:page-name item)) (:item-active style)))
                        :on-click #((:action item) state)
                        :on-mouse-over #(reset! hovered idx)
                        :on-mouse-out #(reset! hovered nil)
                        :key idx} (:label item)]) filtered-items))]]))))
