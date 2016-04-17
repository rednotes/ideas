(ns user
  (:require [mount.core :as mount]
            [ideas-readnotes.figwheel :refer [start-fw stop-fw cljs]]
            ideas-readnotes.core))

(defn start []
  (mount/start-without #'ideas-readnotes.core/repl-server))

(defn stop []
  (mount/stop-except #'ideas-readnotes.core/repl-server))

(defn restart []
  (stop)
  (start))


