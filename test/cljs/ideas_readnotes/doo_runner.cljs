(ns ideas-readnotes.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [ideas-readnotes.core-test]))

(doo-tests 'ideas-readnotes.core-test)

