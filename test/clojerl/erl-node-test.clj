(ns clojerl.erl-node-test
  (:require [clojure.test :refer :all]
            [clojerl.node]))

;; the tests require an epmd running
;; run cd ../erl_ipc; ./start_node.sh first

(deftest test-ipc
  (testing "talk to erlang-node"
    (let [node (clojerl.node/create "clojure_node" "cookie"),
          mbox (clojerl.node/create-mbox node)]
      (clojerl.node/send mbox "erl_node" "node_test"
                         (clojerl.types/tuple [(clojerl.node/self mbox)
                                               (clojerl.types/tuple [:sum, 1,2])]))
      (is (= (:message (clojerl.node/receive mbox)), [:sum, 3]))
      (clojerl.node/close-mbox node mbox)
      (clojerl.node/close node))))
