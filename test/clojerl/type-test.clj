(ns clojerl.type-test
  (:require [clojure.test :refer :all]
            [clojerl.types :as clojerl])
  (:import (com.ericsson.otp.erlang OtpErlangAtom
                                    OtpErlangBinary
                                    OtpErlangDouble
                                    OtpErlangLong
                                    OtpErlangObject
                                    OtpErlangTuple
                                    OtpErlangList
                                    OtpErlangString)
           (java.util Vector)))

;; to-erlang and to-clojure are internal helpers
;; if you just want to use the clojerl but not extend or improve it
;; you better steal from node-test.

(deftest test-to-erlang
  (testing "to-erlang java.lang.Long"
    (is (= (.toString (clojerl/to-erlang 1)) "1")))

  (testing "to-erlang java.lang.Double"
    (is (= (.toString (clojerl/to-erlang 2.0)) "2.0")))

  (testing "to-erlang clojure.lang.Symbol"
    (is (= (.toString (clojerl/to-erlang 'a)) "a")))

  (testing "to-erlang clojure.lang.Keyword"
    (is (= (.toString (clojerl/to-erlang :a)) "a")))

  (testing "to-erlang java.lang.String"
    (is (= (.toString (clojerl/to-erlang "foo")) "\"foo\"")))

  (testing "encode binary"
    (let [v (doto (Vector.)
              (.add 1)
              (.add 2))]
      (is (= (.toString (clojerl/encode-binary v)) "#Bin<257>"))))

  (testing "encode term"
    (is (= (.toString (clojerl/encode-term [1 2 3])) "#Bin<453>")))

  (testing "to-erlang clojure.lang.PersistentArrayMap"
    (is (= (.toString (clojerl/to-erlang {:a :b 'd 1})) "[{a,b},{d,1}]")))

  (testing "to-erlang clojure.lang.PersistentHashMap"
    (is (= (.toString (clojerl/to-erlang {:a :b 'd 1})) "[{a,b},{d,1}]")))

  (testing "to-erlang clojure.lang.PersistentVector"
    (is (= (.toString (clojerl/to-erlang
                       [:a :b 'c 1 2 3 "four"]))
           "[a,b,c,1,2,3,\"four\"]")))

  (testing "to-erlang clojure.lang.PersistentList"
    (is (= (.toString (clojerl/to-erlang
                       (list :a :b 'c 1 2 3 "four")))
           "[a,b,c,1,2,3,\"four\"]"))))


(deftest test-to-clojure
  (testing "to-clojure OtpErlangLong"
    (is (= (clojerl/to-clojure (OtpErlangLong. 1)) 1)))

  (testing "to-clojure OtpErlangDouble"
    (is (= (clojerl/to-clojure (OtpErlangDouble. 1.1)) 1.1)))

  (testing "to-clojure OtpErlangAtom"
    (is (= (clojerl/to-clojure (OtpErlangAtom. "foo")) :foo)))

  (testing "to-clojure OtpErlangString"
    (is (= (clojerl/to-clojure (OtpErlangString. "foo")) "foo")))

  (testing "to-clojure OtpErlangTuple"
    (is (= (clojerl/to-clojure (OtpErlangTuple.
                                (into-array
                                 OtpErlangObject
                                 (map clojerl/to-erlang [1 2 3]))))

           [1 2 3])))

  (testing "decode-binary"
    (let [v (doto (Vector.)
              (.add 1)
              (.add 2))]
      (is (= (clojerl/decode-binary (clojerl/encode-binary v)) v))))

  (testing "decode term"
    (is (= (clojerl/decode-term (clojerl/encode-term [1 2 3])) [1 2 3] )))

  (testing "to-clojure OtpErlangList"
    (is (= (clojerl/to-clojure (OtpErlangList.
                                (into-array
                                 OtpErlangObject
                                 (map clojerl/to-erlang [1 2 3]))))
           [1 2 3]))))
