(ns clj-graph.test.indexed-set-of-sets
  (:use [clj-graph.indexed-set-of-sets] :reload)
  (:use [clojure.test]))

(def item1 #{"a1" "b1" "c1"})
(def item2 #{"a2" "b2" "c2"})
(def item3 #{"a1" "b1" "c2"})

(def empty-set (indexed-create))
(def set1 (-> (indexed-create)
            (indexed-add item1)))
(def set2 (-> (indexed-create)
            (indexed-add item2)))
(def set12 (-> (indexed-create)
             (indexed-add item1)
             (indexed-add item2)))
(def set123 (-> (indexed-create)
              (indexed-add item1)
              (indexed-add item2)
              (indexed-add item3)))

(deftest test-add
  (is (not (= set1 set2))
      "Adding an item to a set changes the set.")
  (is (not (= set1 set12))
      "Adding another item to a set changes the set.")
  (is (= set12 (indexed-add set2 item1))
      "Order of adding does not matter."))

(deftest test-contains
  (is (indexed-contains? set1 item1)
      "A set contains the item placed in it.")
  (is (and (indexed-contains? set12 item1)
           (indexed-contains? set12 item2))
      "A larger set contains both items placed in it.")
  (is (not (indexed-contains? set1 item2))
      "A set does not contain an item that was not placed in it."))

(deftest test-remove
  (is (and (= set1 (indexed-remove set12 item2))
           (= set2 (indexed-remove set12 item1)))
      "Remove is the inverse of add.")
  (is (= empty-set (indexed-remove set1 item1))
      "Removing everything results in the empty set."))

(deftest test-get-by-index
  (is (= #{} (get-by-index empty-set "a1"))
      "get-by-index returns empty set when no relevant available.")
  (is (= #{item1} (get-by-index set12 "a1"))
      "get-by-index finds the matching set.")
  (is (= #{item1 item3} (get-by-index set123 "b1"))
      "get-by-index can find more than one result."))
