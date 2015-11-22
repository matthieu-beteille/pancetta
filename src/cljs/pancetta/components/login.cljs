(ns pancetta.components.login)

(defonce style {:link {:cursor "pointer"}})

(defn login! [state]
  (.authWithOAuthPopup (:root @state) "facebook"
   (fn [error user]
     (when (nil? error)
      (swap! state assoc :user (aget user "uid"))))))

(defn login-component [state]
  (fn []
    [:div
      [:h2 "Login Page"]
      [:div
        [:a {:style (:link style) :on-click #(login! state)} "Login with Facebook"]]]))
