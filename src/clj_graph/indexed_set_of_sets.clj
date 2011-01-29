(ns clj-graph.indexed-set-of-sets
  (:require clojure.set))

(defn indexed-create []
  {:set #{}
   :index {}})

(defn indexed-add [indexed item]
  {:set (conj (:set indexed) item)
   :index (apply merge-with clojure.set/union (:index indexed)
                 (map (fn [k] {k #{item}})
                      item))})

(defn indexed-contains? [indexed item]
  (contains? (:set indexed) item))

(defn indexed-remove [indexed item]
  (if (indexed-contains? indexed item)
    (let [new-set (disj (:set indexed) item)
          new-index (apply merge-with clojure.set/difference (:index indexed)
                           (map (fn [k] {k #{item}})
                                item))
          keys-no-longer-used (filter (fn [k] (= #{} (new-index k)))
                                      item)]
      {:set new-set
       :index (apply dissoc new-index keys-no-longer-used)})
    indexed))

(defn get-by-index [indexed k]
  (set ((:index indexed) k)))
