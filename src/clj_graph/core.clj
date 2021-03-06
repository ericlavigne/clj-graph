(ns clj-graph.core
  (:use clojure.contrib.combinatorics
        clojure.contrib.math
        clojure.set
        clj-graph.indexed-set-of-sets))

(defn random-graph
  "Generate a random graph with a given number of nodes and approximate density.
    All configuration parameters are optional, so random-graph can be called
    with no arguments.

    Example:
        (random-graph {:num-nodes 5 :density 0.5})
          => {0 #{1 4},
              1 #{0 3},
              2 #{3},
              3 #{1 2 4},
              4 #{0 3}}"
  ([] (random-graph {}))
  ([options]
     (let [num-nodes (or (:num-nodes options) 10)
           nodes (range num-nodes)
           possible-edges (combinations nodes 2)
           possible-edges (if possible-edges possible-edges [])
           medium-density (min 1.0 (/ (expt num-nodes 4/3)
                                      (max 1 (count possible-edges))
                                      2))
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

(defn- sum [col]
  (reduce + 0 col))

(defn density
  "Calculate the density of a graph. Density is proportional to the number of
   edges. A graph with density 0 has no edges. A graph with density 1 has each
   node connected to every other node. Densities outside the range [0,1] are
   not possible."
  [graph]
  (let [num-nodes (count graph)
        num-edges (sum (map count (vals graph)))
        num-possible-edges (* num-nodes (dec num-nodes))]
    (if (< num-nodes 2) 1.0 (/ (* 1.0 num-edges)
                               num-possible-edges))))

(defn concatmap [f & args]
  (apply concat (apply map f args)))

(defn cliques
  "Find all cliques in the graph, and return them as a set of sets of nodes."
  [graph]
  (loop [remaining-nodes (keys graph)
         sub-cliques (indexed-create)]
    (let [node (first remaining-nodes)
          connections (graph node)
          matching-sub-cliques (if (< (count connections)
                                      (count (:set sub-cliques)))
                                 (concatmap (fn [connection]
                                              (get-by-index sub-cliques connection))
                                            connections)
                                 (filter (fn [sub-clique]
                                           (not (empty? (intersection sub-clique
                                                                      connections))))
                                         (:set sub-cliques)))
          new-sub-cliques (reduce (fn [sub-cliques match]
                                    (let [overlap (intersection match
                                                                connections)
                                          new-sub-clique (conj overlap node)]
                                      (if (= match overlap)
                                        (indexed-add (indexed-remove sub-cliques
                                                                     match)
                                                     new-sub-clique)
                                        (indexed-add sub-cliques
                                                     new-sub-clique))))
                                  sub-cliques
                                  (if (empty? matching-sub-cliques)
                                    [#{node}]
                                    matching-sub-cliques))]
      (if (empty? (rest remaining-nodes))
        (set (filter (fn [clique] (> (count clique) 2))
                     (:set new-sub-cliques)))
        (recur (rest remaining-nodes)
               new-sub-cliques)))))
