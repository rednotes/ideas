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
        {:on-click #(swap! collapsed? not)} [:i.fa.fa-bars]]
       [:div.collapse.navbar-toggleable-xs
        (when-not @collapsed? {:class "in"})
        [:a.navbar-brand {:href "#/"} "ideas.readnot.es"]
        [:ul.nav.navbar-nav
         [nav-link "#/" "Ideas" :home collapsed?]
         [nav-link "#/about" "About" :about collapsed?]]]])))

(defn set-html [html]
  {:dangerouslySetInnerHTML {:__html html}})

(defn about-page []
  [:div.container
   [:div.row
    [:div.col-md-3]
    [:div.col-md-6
     "this is the story of ideas-readnotes... ork in progress"]]])

(def new-title (r/atom ""))
(def new-description (r/atom ""))

(defn send-idea [title description]
  (POST "/api/ideas" {:params {:id -1 :title title :description description}}))

(def ideas (r/atom []))

(def update-status (r/atom ""))

(defn update-ideas [response]
  (do (reset! ideas (sort #(> (:id %1) (:id %2)) response))
      (reset! update-status "btn-success")
      (js/setTimeout #(reset! update-status "") 1000)))

(defn handle-error [info]
  (do (reset! update-status "btn-danger")
      (js/setTimeout #(reset! update-status "") 1000)))

(defn update-ideas-list []
  (GET "/api/ideas" {:response-format :json
                     :keywords? true
                     :handler update-ideas
                     :error-handler handle-error}))
(update-ideas-list)

(defn some-form []
  [:div.form-horizontal
   [:div.form-group.row
    [:div.col-sm-12
     [:input#title.form-control
      {:type "text"
       :value @new-title
       :on-change #(reset! new-title (-> % .-target .-value))
       :placeholder "Idea (make the most out of 140 characters)"}]]]
   [:div.form-group.row
    [:div.col-sm-12
     [:textarea#description.form-control
      {:rows 5
       :placeholder "Explanation of the idea (markup with markdown)"
       :value @new-description
       :on-change #(reset! new-description (-> % .-target .-value))}]]]
   [:div.form-group.row
    [:div.col-sm-12
     [:div (set-html (md->html @new-description))]]]
   [:div.form-group.row
    [:div.col-sm-12
     [:button.btn.btn-block.btn-primary-outline.col-sm-12
      {:on-click #((do (send-idea @new-title @new-description)
                       (reset! new-title "")
                       (reset! new-description "")
                       (js/setTimeout (fn [] (update-ideas-list)) 100)))}
      "Send"]]]])

;; (println ideas)
(def current-idea (r/atom ""))


(defn show-idea [idea]
  (reset! current-idea idea))

(defn idea-template [idea]
  [:div
   [:div.row ; {:style {:display "inline-block"}}
    [:h3.col-xs-10 "Idea #" (:id idea)]
    [:button.btn.btn-secondary.btn-sm.col-xs-1
     {:data-toggle "modal"
      :data-target "#ideaModal"
      :on-click #(show-idea idea)}
     [:i.fa.fa-lightbulb-o]]
    ]
   [:p (:title idea)]])

(defn modal []
  [:div.modal.fade {:id "ideaModal"}
   [:div.modal-dialog {:role "document"}
    [:div.modal-content
     [:div.modal-header "#" (:id @current-idea) ": "
      (:title @current-idea)]
     [:div.modal-body (set-html (md->html (:description @current-idea)))]
     [:div.modal-footer
      [:button.btn.col-xs-12.btn-secondary {:data-dismiss "modal"} "Close"]]]]])

(defn status-info []
  [:div.row.text-xs-center
   [:h2 "Я заработал на ваших идеях " [:strong (count @ideas)] " долларов"]])

(defn home-page []
  [:div.container
   [:div.row
    [:div.col-md-6
     (when-let [info (session/get :info)]
       (set-html (md->html info)))]
    [:div.col-md-6 [some-form]]]
   [:div.row
    [:div.col-md-12
     [:button.btn.btn-secondary.btn-block.col-xs-12
      {:class @update-status
       :on-click update-ideas-list}
      [:i.fa.fa-refresh]]]]
   [:hr]
   [status-info]
   [:hr]
   [modal]
   [:div.row
    [:div (for [item @ideas]
            ^{:key {:id item}}
            [:div.col-md-4 (idea-template item)])]]
   [:hr]
   ])

(defn idea-page []
  [:div.container
   [:div (str (session/get :id) " idea page")]
   ])

(def pages
  {:home #'home-page
   :about #'about-page
   :idea #'idea-page})

(defn page []
  [(pages (session/get :page))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :page :home))

(secretary/defroute "/about" []
  (session/put! :page :about))

(secretary/defroute #"/idea/(\d+)" [id]
  (do (session/put! :page :idea)
      (session/put! :id id)))
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
  (GET (str js/context "/info") {:handler #(session/put! :info %)})
  ;; (GET "/api/ideas" {:response-format :json
  ;;                    :keywords? true
  ;;                    :handler #(session/put! :ideas %)})
  )

(defn mount-components []
  (r/render [#'navbar] (.getElementById js/document "navbar")) 
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
