(ns clojerl.node-test
  (:require [clojure.test :refer :all]
            [clojerl.node]))

;; the tests require an epmd running

(deftest test-interface
  (testing "create/close-node"
    (let [node (clojerl.node/create "test")]
      (is (not= node nil))
      (clojerl.node/close node)))
  (testing "create/close-mbox"
    (let [node (clojerl.node/create "test")
          mbox (clojerl.node/create-mbox node)]
      (is (not= mbox nil))
      (clojerl.node/close-mbox node mbox)
      (clojerl.node/close node))))


(defn receive-msg [mbox]
  (let [msg (clojerl.node/receive mbox)]
    (when msg
      (:message msg))))

(deftest test-mbox
  ;;serializes and deserializes various clojure types from erlang-format
  ;;to clojure format. This is done by sending the data to an
  ;;otp-mailbox. No extern erlang process is used in this test.
  ;;The test talks to itself using malboxes just like two erlang-processes
  ;;talk to each other in an erlang-program.

  (testing "send/receive string between clojure mboxes"
    (let [node (clojerl.node/create "test")
          box-a (clojerl.node/create-mbox node "box-a")
          box-b (clojerl.node/create-mbox node "box-b")]

      ;; strings and numbers are straight forward
      (clojerl.node/send box-a "box-b" "hello from box-a")
      (is (= (receive-msg box-b) "hello from box-a"))

      (clojerl.node/send box-a "box-b" 1)
      (is (= (receive-msg box-b) 1))

      (clojerl.node/send box-a "box-b" 3.14)
      (is (= (receive-msg box-b) 3.14))


      ;; atoms and keywords are sent to erlang as erlang-atoms
      ;; erlang atoms are represeted in clojure as keywords.
      (clojerl.node/send box-a "box-b" 'atom_from_box_a)
      (is (= (receive-msg box-b) :atom_from_box_a))

      (clojerl.node/send box-a "box-b" :keyword_from_box_a)
      (is (= (receive-msg box-b) :keyword_from_box_a))

      ;;clojure-lists and clojure-vectors
      ;;are sent to erlang as erlang-lists.
      ;;erlang-lists and erlang-tuples
      ;;are represented as clojure-vectors

      (clojerl.node/send box-a "box-b" [:vector 1 2 3])
      (is (= (receive-msg box-b) [:vector 1 2 3]))

      (clojerl.node/send box-a "box-b" (list :list 1 2 3))
      (is (= (receive-msg box-b) [:list 1 2 3]))

      (clojerl.node/send box-a "box-b" (list :list [1 2 3]))
      (is (= (receive-msg box-b) [:list [1 2 3]]))


      ;; erlang knows no equivalent to clojure-maps.
      ;; a clojure-map is sent to erlang as a proplist
      ;; erlang-proplists are represented in clojure
      ;; as clojure-vectors that contain clojure-vectors
      ;; with two elements

      (clojerl.node/send box-a "box-b" {:foo [1
                                              (list 2 3.14)
                                              {:bar "bar"}]})
      (is (= (receive-msg box-b) [[:foo [1
                                         [2 3.14]
                                         [[:bar "bar"]]]]]))
      (clojerl.node/close-mbox node box-a)
      (clojerl.node/close-mbox node box-b)
      (clojerl.node/close node))))
