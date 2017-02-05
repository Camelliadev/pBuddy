(ns irc-bot.socket
  (:require [clojure.java.io :as io])
  (:require [clojure.string :as str])
  (:require [clojure.core.async :refer :all])
  (import (java.net Socket))
  (:gen-class))
(defn open-socket
    "Creates a socket and connects to host on port"
    [host port]
    (Socket. host port))
(defn close-socket
    "Closes the socket"
    [socket]
    (.close socket))
(defn write-line
    "Write line to socket"
    [socket msg]
    (let [output (io/writer socket)]
               (.write output (format "%s\r\n" msg))
               (.flush output)))
(defn read-lines
    "Read lines from a socket and sends them to the channel to be processed later"
    [bot chan]
    (go (with-open [input (io/reader (:sock bot))]
               (doseq [line (line-seq input)]
                      (if (str/starts-with? line "PING")
                          (write-line (:sock bot) (str/replace line #"PING" "PONG"))
                          (>! chan line))))))