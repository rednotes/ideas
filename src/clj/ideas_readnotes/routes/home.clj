(ns ideas-readnotes.routes.home
  (:require [clojure.java.io :as io]
            [compojure.api.sweet :as s]
            [compojure.core :refer [context defroutes GET POST routes]]
            [ideas-readnotes.db.core :as db]
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
  {:id id :title "Основная мысль" :description (str "

### heading ####





 <a href=\"google.ru\">link</a>
* list
* of items

описание идеи " id)})

(def aphorisms
  ["Идеи тем проще овладевают массами, чем они проще. Сергей Скотников"
   "Идеи воспламеняют друг друга, подобно электрическим искрам. Фридрих Энгельс"
   "Идеи овладевают массами в извращенной форме. Акрам Муртазаев"
   "Деятельность человека пуста и ничтожна, когда не одушевлена идеею. Николай Гаврилович Чернышевский"
   "Есть нечто более сильное, чем все на свете войска: это идея, время которой пришло. Виктор Мари Гюго"
   "Основная идея всегда должна быть недосягаемо выше, чем возможность ее исполнения. Федор Михайлович Достоевский"])

(defn get-aphorism []
  (j-response (rand-nth aphorisms)))

(defn get-idea [id]
  (db/get-idea {:id id}))

(defn create-idea [idea]
  (db/create-idea! (assoc idea :status 0)))

(defn get-all-ideas []
  (j-response (db/get-all-ideas)))

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
    (s/GET "/aphorism" [] (get-aphorism))
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

