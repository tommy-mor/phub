(ns front.core
  (:require
   [reagent.core :as reagent]
   ))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars

(defonce textbox
  (reagent/atom "write message here"))

(defonce messages
  (reagent/atom []))
(defonce counter
  (reagent/atom 0))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Page
(defn send-message [message]
  (swap! counter inc)
  (reset! messages (conj @messages message))
  (reset! textbox ""))

(defn message-list [messages]
  [:div
     (for [item messages]
       (do
         (println item)
         ^{:key (:key item)} [:div (:text item) ]))])

(defn page []
  (fn []
    [:div.mainscreen
     "Welcome to reagent-figwheel."
     [message-list @messages]
     [:div.textinput
      [:input.textinput {:auto-focus true :type "text"
                         :value @textbox
                         :on-change #(reset! textbox (-> % .-target .-value))
                         :on-key-down #(case (.-which %)
                                         13 (send-message {:text @textbox :key @counter})
                                         nil)}]]]))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Initialize App

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "dev mode")
    ))

(defn reload []
  (reagent/render [page]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (dev-setup)
  (reload))