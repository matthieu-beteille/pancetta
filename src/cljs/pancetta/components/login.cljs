(ns pancetta.components.login)

(defonce style {:link {:cursor "pointer"}})

;; facebook login function
(defn login [state]
  (.authWithOAuthPopup (:root @state) "facebook"
   (fn [error user] (swap! state assoc :user (aget user "facebook" "displayName")))))

(defn login-component [state]
  (println state)
  (fn []
    [:div
    [:h2 "Login Page"]
    [:div
      [:a {:style (:link style) :on-click #(login state)} "Login with Facebook"]]]))
