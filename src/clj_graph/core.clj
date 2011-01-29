(ns clj-graph.core
  (:use clojure.contrib.combinatorics
        clojure.contrib.math
        clojure.set))

(defn random-graph
  "Generate a random graph with a given number of nodes and approximate density.

    Example:
        (random-graph {:num-nodes 5 :density 0.5})
          => {0 #{},
              1 #{},
              2 #{},
              3 #{},
              4 #{}}"
  ([] (random-graph {}))
  ([options]
     (let [num-nodes (or (:num-nodes options) 10)
           nodes (range num-nodes)
           possible-edges (combinations nodes 2)
           medium-density (min 1.0 (/ (expt num-nodes 4/3)
                                      (count possible-edges) 2))
           density (or (:density options) medium-density)
           _ (if (or (> 0.0 density) (< 1.0 density))
               (throw (IllegalArgumentException.
                       (str "Density (" density ") should be between 0 and 1."))))
           edges (take (int (* density (count possible-edges)))
                       (shuffle possible-edges))
           graph-without-edges (zipmap nodes (repeat #{}))]
       (reduce (fn [graph [a b]]
                 (merge-with union
                             graph
                             {a #{b}
                              b #{a}}))
               graph-without-edges
               edges))))