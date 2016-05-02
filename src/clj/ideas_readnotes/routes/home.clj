(ns ideas-readnotes.routes.home
  (:require [clojure.java.io :as io]
            [compojure
             [core :refer [context defroutes GET POST routes]]]
            [compojure.api.sweet :as s]
            [ideas-readnotes.layout :as layout]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.swagger
             [swagger2 :refer [swagger-json]]
             [ui :refer [swagger-ui]]]
            [ring.util
             [http-response :as response]
             [json-response :refer [json-response]]]
            [schema.core :as sc]))

(defn j-response [body]
  (response/charset (json-response body) "UTF-8"))

(sc/defschema Idea
  {:id sc/Int
   :title sc/Str
   :description sc/Str})

(defn sample-idea [id]
  {:id id :title "Основная мысль" :description "long long описание"})

(def ideas
  [(sample-idea 1) (sample-idea 2) (sample-idea 3)])

(defn get-idea [id]
  (ideas id))

(defn create-idea [idea]
  (println idea))

(defn get-all-ideas []
  (j-response ideas))



(s/defapi api-routes
  {:swagger {:ui "/api-docs"
             ;; :formats [:json-kw]
             :spec "/swagger.json"
             :data {:info {:version "0.0.1"
                           :title "Ideas API"
                           :description "simple ideas api"}
                    :tags [{:name "api" :description "Simple api"}]}}}
  (s/context "/api" []
    :tags ["api"]
    (s/GET "/ping" [] (response/ok {:ping "pong"}))
    (s/context "/ideas" []
      (s/GET "/" []
        (get-all-ideas))
      (s/context "/:id" []
        :path-params [id :- Long]
        (s/GET "/" []
          ;; :return Idea
          (j-response (get-idea id))))
      (s/POST "/" []
        :body [idea Idea]
        (response/ok (create-idea idea)))
      )))

(defn home-page []
  (layout/render "home.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  ;; (context "/api" [] api)
  (GET "/docs" [] (response/ok (-> "docs/docs.md" io/resource slurp)))
  (GET "/info" [] (response/charset (response/ok (-> "docs/info.md" io/resource slurp)) "UTF-8")))

