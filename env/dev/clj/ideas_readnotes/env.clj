(ns ideas-readnotes.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [ideas-readnotes.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[ideas-readnotes started successfully using the development profile]=-"))
   :middleware wrap-dev})
