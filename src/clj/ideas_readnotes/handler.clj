(ns ideas-readnotes.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [ideas-readnotes.layout :refer [error-page]]
            [ideas-readnotes.routes.home :refer [api-routes home-routes]]
            [compojure.route :as route]
            [ideas-readnotes.middleware :as middleware]))

(def app-routes
  (routes
   #'api-routes
    (wrap-routes #'home-routes middleware/wrap-csrf)
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))

(defn app [] (middleware/wrap-base #'app-routes))
