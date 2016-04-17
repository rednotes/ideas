(ns ideas-readnotes.core-test
  (:require [cljs.test :refer-macros [is are deftest testing use-fixtures]]
            [reagent.core :as reagent :refer [atom]]
            [ideas-readnotes.core :as rc]))

(deftest test-home
  (is (= true true)))

