(ns ideas-readnotes.db.core
  (:require [clojure.java.jdbc :as jdbc]
            [conman.core :as conman]
            [ideas-readnotes.config :refer [env]]
            [luminus-migrations.core :refer [migrate]]
            [mount.core :refer [defstate]]))

(def pool-spec
  {:adapter    :postgresql
   :init-size  1
   :min-idle   1
   :max-idle   4
   :max-active 32}) 

(defn connect! []
  (conman/connect!
   (assoc
    pool-spec
    :jdbc-url (env :database-url))))

(defn disconnect! [conn]
  (conman/disconnect! conn))

(defstate ^:dynamic conn
  :start (connect!)
  :stop (disconnect! conn))

(conman/bind-connection conn "sql/queries.sql")

;; (create-idea! {:title "Create database backend" :description "Create datbase backend with postgresql" :status 0})

;; (migrate ["migrate"] (env :datbase-url))
