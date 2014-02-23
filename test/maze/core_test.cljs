(ns maze.core-test
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest with-test run-tests testing test-var)])
  (:require [cemerick.cljs.test :as t]
            [maze.core :as core]))

(deftest test-neighbors
  (testing "returns all neighbors for a location"
    (is (= #{[2 1] [3 2] [2 3] [1 2]}
           (core/neighbors [2 2])))
    (is (= #{[0 -1] [1 0] [0 1] [-1 0]}
           (core/neighbors [0 0])))))

(deftest test-visitable-neighbors
  (testing "returns all neighbors within bounds of maze"
    (is (= #{[1 0] [0 1]}
           (core/visitable-neighbors [0 0] #{} 5)))
    (is (= #{[4 3] [3 4]}
           (core/visitable-neighbors [4 4] #{} 5))))
  (testing "returns all unvisited neighbors"
    (is (= #{[2 3] [1 2]}
           (core/visitable-neighbors [2 2] #{[2 1] [3 2]} 5)))))

(defn dumb-next-location [location visited size]
  (cond
    (= [0 0] location) (if (visited [1 0]) nil [1 0])
    (= [1 0] location) (if (visited [1 1]) nil [1 1])
    (= [1 1] location) (if (visited [0 1]) nil [0 1])
    (= [0 1] location) nil))

(deftest test-generate-maze
  (testing "visits all locations"
    (is (= 1 (count
               (:visited
                 (core/generate-maze {:visited #{} :path [[0 0]] :doors #{} :size 1})))))
    (is (= 4 (count
               (:visited
                 (core/generate-maze {:visited #{} :path [[0 0]] :doors #{} :size 2}))))))
  (testing "path is empty"
    (is (= 0 (count
               (:path
                 (core/generate-maze {:visited #{} :path [[0 0]] :doors #{} :size 1}))))))
  (testing "carves doors as expected"
    (is (= #{#{[0 0] [1 0]} #{[1 0] [1 1]} #{[0 1] [1 1]}}
           (:doors (core/generate-maze {:visited #{}
                                        :path [[0 0]]
                                        :doors #{}
                                        :size 2
                                        :next-location-fn dumb-next-location }))))))

(deftest fully-walled-grid-test
  (is (= #{#{[0 0] [0 1]} #{[0 0] [1 0]} #{[1 0] [1 1]} #{[1 1] [0 1]}}
         (core/fully-walled-grid 2))))