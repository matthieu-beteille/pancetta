(ns pancetta.components.home)

(defn home-component []
  [:div
    [:h1 {:on-click #(.log js/console "click")} "HOME PAGE"]])
