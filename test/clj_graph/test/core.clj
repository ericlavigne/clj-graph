(ns clj-graph.test.core
  (:use [clj-graph.core] :reload)
  (:use [clojure.test])
  (:use clojure.contrib.math))

(defn approx= [tolerance]
  (fn [& args]
    (if (< (count args) 2)
      true
      (> tolerance (abs (- (apply max args)
                           (apply min args)))))))

(deftest test-random
  (is (< 0 (count (random-graph)))
      (str "random-graph creates a graph with at least one node "
           "when no options specified."))
  (is (= 20 (count (random-graph {:num-nodes 20})))
      "random-graph creates a graph with the specified number of nodes.")
  (is (= 0 (count (random-graph {:num-nodes 0})))
      "Possible to create random-graph with no nodes.")
  (is (= 1 (count (random-graph {:num-nodes 1})))
      "Possible to create random-graph with only one node.")
  (is (= 2 (count (random-graph {:num-nodes 2})))
      "Possible to create random-graph with only two nodes."))

(deftest test-density
  (is ((approx= 0.05) 0.4 (density (random-graph {:num-nodes 100 :density 0.4})))
      "Density of random-graph approximately matches requested density.")
  (is (and (density (random-graph {:num-nodes 0}))
           (density (random-graph {:num-nodes 1}))
           (density (random-graph {:num-nodes 2})))
      "Can calculate densities of very small graphs."))

(deftest test-cliques
  (let [graph-with-three-cliques {1 #{2 3 8}
                                  2 #{1 3 4}
                                  3 #{1 2 4 5}
                                  4 #{2 3}
                                  5 #{3 6 7}
                                  6 #{5 7}
                                  7 #{5 6}
                                  8 #{1 9}
                                  9 #{8}}]
    (is (= (cliques graph-with-three-cliques)
           #{#{1 2 3}
             #{2 3 4}
             #{5 6 7}})
        "calculate cliques for small graph with three small cliques, two of which overlap.")))
  