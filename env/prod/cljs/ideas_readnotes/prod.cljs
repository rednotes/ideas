(ns ideas-readnotes.app
  (:require [ideas-readnotes.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
