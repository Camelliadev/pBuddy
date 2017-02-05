(ns irc-bot.bot
    (:require [irc-bot.socket :refer :all])
    (:require [irc-bot.irc :refer :all])
    (:require [clojure.core.async :refer :all])
    (:require [clojure.string :as str])
    (:require [clojure.java.io :as io])
    (:gen-class))
(defn third
    "Pulls the third element out"
    [x]
    (second(next x)))
(defn parse-message
    "Parses incoming irc messages"
    [bot line]
    (let [parts (str/split line #" " 4)
          user (first parts)
          hostname (last (str/split user #"@"))
          nickname (subs (first (str/split user #"!")) 1)
          message (if (.contains (last parts) ":")
                      (subs (last parts) 1)
                      (last parts))
          command (second parts)
          origin (third parts )]
      {:nick nickname :hostname hostname :command command :origin origin :message message}))
(defn log
    "Writes messages to a text file"
    [bot line]
    (let [logfolder "logs/"
          message (parse-message bot line)
          channel (:origin message)
          source (if (= channel (:nick bot)) (:nick message) channel)
          logfile (str  logfolder source ".txt")
          entry (format "%s : %s\n" (:nick message) (:message message))
          command (:command message)]
      (io/make-parents logfile)
      (if (= command "PRIVMSG") (spit logfile entry :append true))))
(defn hello
    "Says hello"
    [bot line]
    (let [message (parse-message bot line)
          channel (:origin message)
          source (if (= channel (:nick bot)) (:nick message) channel)]
    (if (.contains (str/lower-case line) "hello pbuddy")
        (say bot (format "Hello %s" (:nick message)) source ))))
(defn -main
  "Connects to the network and prints out lines"
  [& args]
  (def pBuddy (create-bot "pBuddy" "irc.rizon.net" 6667))
  (def lines (chan))
  (start-bot pBuddy lines ["#pBuddy"][log hello]))
