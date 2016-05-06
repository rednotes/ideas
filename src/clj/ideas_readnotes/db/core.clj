(ns ideas-readnotes.db.core
  (:require [clojure.java.jdbc :as jdbc]
            [conman.core :as conman]
            [ideas-readnotes.config :refer [env]]
            [luminus-migrations.core :refer [migrate]]
            [mount.core :refer [defstate]]))

(def pool-spec
  {:adapter :postgresql
   :jdbc-url (or "postgresql://localhost/ideas" (env :database-url))
   :init-size 1
   :min-idle 1
   :max-idle 4
   :max-active 32})

(defstate ^:dynamic *db*
  :start (conman/connect! pool-spec)
  :stop (conman/disconnect! *db*))

(conman/bind-connection *db* "sql/queries.sql")

;; (create-idea! {:title "Create database backend" :description "Create datbase backend with postgresql" :status 0})

(migrate ["migrate"] (:jdbc-url pool-spec))
