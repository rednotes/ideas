(ns ideas-readnotes.core
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [ideas-readnotes.ajax :refer [load-interceptors!]]
            [ajax.core :refer [GET POST]])
  (:import goog.History))

(defn nav-link [uri title page collapsed?]
  [:li.nav-item
   {:class (when (= page (session/get :page)) "active")}
   [:a.nav-link
    {:href uri
     :on-click #(reset! collapsed? true)} title]])

(defn navbar []
  (let [collapsed? (r/atom true)]
    (fn []
      [:nav.navbar.navbar-light.bg-faded
       [:button.navbar-toggler.hidden-sm-up
        {:on-click #(swap! collapsed? not)} "☰"]
       [:div.collapse.navbar-toggleable-xs
        (when-not @collapsed? {:class "in"})
        [:a.navbar-brand {:href "#/"} "ideas-readnotes"]
        [:ul.nav.navbar-nav
         [nav-link "#/" "Home" :home collapsed?]
         [nav-link "#/about" "About" :about collapsed?]]]])))

(defn set-html [html]
  {:dangerouslySetInnerHTML {:__html html}})

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-3]
    [:div.col-md-6
     "this is the story of ideas-readnotes... work in progress"]]])

(def some-form
  [:form.form-horizontal
   [:div.form-group.row
    [:div.col-sm-12
     [:input#title.form-control {:type "text" :placeholder "Idea (make the most out of 140 characters)"}]
     ]
    ]
   [:div.form-group.row
    [:div.col-sm-12
     [:textarea#description.form-control {:rows 5 :placeholder "Explanation of the idea (markup with markdown)"}]]
    ]
   [:div.form-group.row
    [:div.col-sm-12
     [:button.btn.btn-block.btn-primary-outline.col-sm-12 {:type "submit"} "Send"]]]])

(def sample-idea
  [:div [:h2 "Idea #0"]
  [:p "Sample idea description in 140 symbols, explanation hidden"]])

(def sample-idea-2
  [:div [:h2 "Idea #n"]
   [:p "aoe uasoetnh iaotsh aoseunth uasonet hu Sample idea description in 140 symbols, explanation hidden"]])

(defn home-page []
  [:div.container
   [:div.row
    [:div.col-md-6
     (when-let [info (session/get :info)]
       (set-html (md->html info)))]
    [:div.col-md-6 some-form]]
   [:div.row
    [:div.col-md-4 sample-idea]
    [:div.col-md-4 sample-idea-2]
    [:div.col-md-4 sample-idea]
    [:div.col-md-4 sample-idea]
    [:div.col-md-4 sample-idea]
    [:div.col-md-4 sample-idea]
    [:div.col-md-4 sample-idea]
    ]]
  )

(def pages
  {:home #'home-page
   :about #'about-page})

(defn page []
  [(pages (session/get :page))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :page :home))

(secretary/defroute "/about" []
  (session/put! :page :about))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
        (events/listen
          HistoryEventType/NAVIGATE
          (fn [event]
              (secretary/dispatch! (.-token event))))
        (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET (str js/context "/docs") {:handler #(session/put! :docs %)})
  (GET (str js/context "/info") {:handler #(session/put! :info %)}))

(defn mount-components []
  ;; (r/render [#'navbar] (.getElementById js/document "navbar")) 
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
