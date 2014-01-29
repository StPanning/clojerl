(ns clojerl.node
  (:require [clojerl.types :as erltypes])
  (:import (com.ericsson.otp.erlang OtpNode
                                    OtpMsg)))

(defn create
  ([node-name]
     (OtpNode. node-name))
  ([node-name cookie]
     (OtpNode. node-name cookie))
  ([node-name cookie port]
     (OtpNode. node-name cookie port)))

(defn close [node]
  (.close node))

(defn create-mbox
  ([node]
     (.createMbox node))
  ([node name]
     (.createMbox node name)))

(defn close-mbox
  ([node mbox]
     (.closeMbox node mbox))
  ([node mbox reason]
     (.closeMbox node mbox reason)))

(defn- decode-msg [msg]
  (let [type_map {OtpMsg/sendTag  :send
                  OtpMsg/linkTag  :link
                  OtpMsg/exitTag  :exit
                  OtpMsg/exit2Tag :exit2}]
    {:recipient (or (.getRecipient msg)
                    (.getRecipientName msg))
     :type      (type_map (.type msg))
     :sender    (.getSenderPid msg)
     :message   (erltypes/to-clojure (.getMsg msg))}))

(defn receive
  ([mbox]
     (let [msg (.receiveMsg mbox)]
           (when msg (decode-msg msg))))
  ([mbox timeout]
     (let [msg (.receiveMsg mbox timeout)]
       (when msg (decode-msg msg)))))

(defn send
  ([mbox recipient message]
     (.send mbox recipient (erltypes/to-erlang message)))
  ([mbox recipient node message]
     (.send mbox recipient node (erltypes/to-erlang message))))

(defn self [mbox]
  (.self mbox))
