(ns clojerl.types
  (:import (com.ericsson.otp.erlang OtpErlangAtom
                                    OtpErlangDouble
                                    OtpErlangLong
                                    OtpErlangObject
                                    OtpErlangTuple
                                    OtpErlangList
                                    OtpErlangString)))

(defprotocol ErlangToClojure
  (to-clojure [e]))

(defprotocol ClojureToErlang
  (to-erlang [e]))

(defn- container-to-erlang [c]
  (let [erl-c (map to-erlang c)]
    (OtpErlangList. (into-array OtpErlangObject erl-c))))

(extend-protocol ClojureToErlang
  java.lang.Long
  (to-erlang [e] (OtpErlangLong. e))

  java.lang.Double
  (to-erlang [e] (OtpErlangDouble. e))

  clojure.lang.Symbol
  (to-erlang [e] (OtpErlangAtom. (name e)))

  clojure.lang.Keyword
  (to-erlang [e] (OtpErlangAtom. (name e)))

  java.lang.String
  (to-erlang [e] (OtpErlangString. e))

  clojure.lang.MapEntry
  (to-erlang [e]
    (OtpErlangTuple. (into-array OtpErlangObject (map to-erlang e))))

  clojure.lang.PersistentArrayMap
  (to-erlang [e]
    (container-to-erlang e))

  clojure.lang.PersistentHashMap
  (to-erlang [e]
    (container-to-erlang e))

  clojure.lang.PersistentList
  (to-erlang [e]
    (container-to-erlang e))

  clojure.lang.PersistentVector
  (to-erlang [e]
    (container-to-erlang e)))


(extend-protocol ErlangToClojure
  OtpErlangLong
  (to-clojure [e] (.longValue e))

  OtpErlangDouble
  (to-clojure [e] (.doubleValue e))

  OtpErlangAtom
  (to-clojure [e] (keyword (.atomValue e)))

  OtpErlangString
  (to-clojure [e] (.stringValue e))

  OtpErlangTuple
  (to-clojure [e]
    (mapv to-clojure (.elements e)))

  OtpErlangList
  (to-clojure [e]
    (mapv to-clojure (.elements e))))