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

(defonce list-of-ppl (reagent/atom ["tommy" "chris" "christopher" "chistopherson"]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Page
(defn scroll-to-bottom []
  (let [a (.getElementById js/document "back")]
    (set! (.-scrollTop a) 9999999)))

(defn send-message [message]
  (swap! counter inc)
  (reset! messages (conj @messages message))
  (reset! textbox "")
  (scroll-to-bottom))

;; change to work when its not first character
(defn auto-complete-box [textbox]
  [:div
   (if (= (first textbox) "@")
     [:div.autobox [:pre
                    (for [item @list-of-ppl]
                           (str item "\n"))]])])

(defn message-list [messages]
  [:div
   (for [item messages]
     (do
       ^{:key (:key item)} [:div (:text item) ]))])

(defn page []
  (fn []
    [:div#back.mainscreen
     [message-list @messages]
     [auto-complete-box @textbox]
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
