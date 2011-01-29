(ns clj-graph.test.core
  (:use [clj-graph.core] :reload)
  (:use [clojure.test]))

(deftest test-random
  (is (< 0 (count (random-graph)))
      (str "random-graph creates a graph with at least one node "
           "when no options specified."))
  (is (= 20 (count (random-graph {:num-nodes 20})))
      "random-graph creates a graph with the specified number of nodes."))
