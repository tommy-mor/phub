(ns front.core
  (:require
   [reagent.core :as reagent]))
   


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Vars

(defonce textbox
  (reagent/atom "write message here"))

(defonce messages
  (reagent/atom []))

(defonce counter
  (reagent/atom 0))

(defonce completer
  (reagent/atom ""))

(defonce list-of-ppl (reagent/atom ["tommy" "chris" "christopher" "chistopherson"]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Page
;;bad

(defn scroll-to-bottom []
  (let [a (.getElementById js/document "back")]
    (set! (.-scrollTop a) 9999999)))

(defn send-message [message]
  (swap! counter inc)
  (reset! messages (conj @messages message))
  (reset! textbox "")
  (scroll-to-bottom))

(defn getstub [textbox]
  (let [a (clojure.string/join (rest (re-find #"@[^\s]*" (str textbox))))]
    a))

(defn get-cursor-pos []
  (let [a (.getElementById js/document "text")
        b (if (nil? a) 0 (.-selectionStart a))]
    b))

(defn auto-complete-box [textbox]
  (let [selected (filter #(re-find (re-pattern (getstub textbox)) %) @list-of-ppl)]
    (reset! completer (first selected))
    [:div
     (let [frontindex (clojure.string/index-of textbox "@")]
       ;; check if there are any spaces in the substring between @ and the cursor, indicating that we should
       ;; not show the box
       (if (and frontindex (not (clojure.string/includes? (subs textbox frontindex (get-cursor-pos)) " ")))
        [:div.autobox [:pre (for [item selected] (str item "\n"))]]))]))

(defn message-list [messages]
  [:div
   (for [item messages]
     (do
       ^{:key (:key item)} [:div (:text item)]))])
;; replaces the @name text with @namefull as selected by ac
(defn interp [one]
  (clojure.string/replace one (str "@" (getstub one)) (str "@" @completer)))

(defn page []
  (fn []
    (let [complete #(do (.preventDefault %)
                        (swap!
                            textbox interp))]
      [:div#back.mainscreen
       [:div (getstub @textbox)]
       [message-list @messages]
       [:div.textinput
        [:input#text.textinput {:auto-focus true :type "text"
                                :value @textbox
                                :on-change #(reset! textbox (-> % .-target .-value))
                                :on-key-down #(case (.-which %)
                                                13 (send-message {:text @textbox :key @counter})
                                                9 (complete %)
                                                nil)}]]
       [auto-complete-box @textbox]])))




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Initialize App

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "dev mode")))


(defn reload []
  (reagent/render [page]
                  (.getElementById js/document "app")))

(defn ^:export main []
  (dev-setup)
  (reload))
