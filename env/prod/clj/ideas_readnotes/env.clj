(ns ideas-readnotes.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[ideas-readnotes started successfully]=-"))
   :middleware identity})
