(ns pancetta.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [goog.events :as events]
              [goog.history.EventType :as EventType]
              [matchbox.core :as m]
              [pancetta.components.home :as home]
              [pancetta.components.navbar :as navbar]
              [pancetta.components.create :as create]
              [pancetta.components.login :as login]
              [pancetta.components.tickets :as tickets]
              [pancetta.common.ui :as ui])
    (:import goog.History))

;; firebase connection
(defonce firebase-app-name "blazing-fire-3944")
(defonce firebase-url (str "https://" firebase-app-name ".firebaseio.com"))

;; app state
(defonce state (atom {:user nil :root (m/connect firebase-url)}))

;; style
(defonce style {:page {:padding 20
                       :max-width (:viewport-max ui/layout)
                       :min-width (:viewport-min ui/layout)
                       :margin-left "auto"
                       :margin-right "auto"}})

;; layout
(defn current-page []
  [:div
    (when (not= nil (:user @state))
      [navbar/navbar-component state])
    [:div {:style (:page style)}
      (if (= (:user @state) nil)
        [login/login-component state]
        [(:component (session/get :current-page)) state])]])

;; routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page {:name "home" :component #'home/home-component}))

(secretary/defroute "/create" []
  (session/put! :current-page {:name "create" :component #'create/create-component}))

(secretary/defroute "/tickets" []
  (session/put! :current-page {:name "tickets" :component #'tickets/tickets-component}))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     EventType/NAVIGATE
     (fn [event]
       (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (hook-browser-navigation!)
  (mount-root))
