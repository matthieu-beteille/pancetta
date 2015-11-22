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
              [pancetta.components.ticket :as ticket]
              [pancetta.common.ui :as ui])
    (:import goog.History))

;; firebase connection
(defonce firebase-app-name "blazing-fire-3944")
(defonce firebase-url (str "https://" firebase-app-name ".firebaseio.com"))

;; app state
(defonce state (atom {:user nil
                      :root (m/connect firebase-url)}))

;; style
(defonce style {:page {:padding 20
                       :max-width (:viewport-max ui/layout)
                       :min-width (:viewport-min ui/layout)
                       :margin-left "auto"
                       :margin-right "auto"}})

;; layout
(defn current-page []
  (let [current-page (session/get :current-page)
        is-public-page (not (:private current-page))
        is-logged-in (not (nil? (:user @state)))]
    [:div
      [navbar/navbar-component state]
      [:div {:style (:page style)}
        (if (or is-logged-in is-public-page)
          [(:component current-page) state]
          [login/login-component state])]]))

;; routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :current-page {:name "home"
                               :component #'home/home-component}))

(secretary/defroute "/create" []
  (session/put! :current-page {:name "create"
                               :component #'create/create-component
                               :private true}))

(secretary/defroute "/tickets" []
  (session/put! :current-page {:name "tickets"
                               :component #'tickets/tickets-component
                               :private true}))

(secretary/defroute "/ticket/:user-id/:id" [user-id id]
  (session/put! :current-page {:name "ticket"
                               :component (partial #'ticket/ticket-component user-id id)}))

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
