(ns irc-bot.irc
    (:require [irc-bot.socket :refer :all])
    (:require [clojure.core.async :refer :all])
    (:require [clojure.string :as str])
    (:gen-class))
(defn create-bot
    "Creates a bot"
    [nick server port]
    {:nick nick :server server :port port :sock (open-socket server port)})
    
(defn identify
    "Identies the bot to the server and returns bot"
    [bot]
    (let
        [ nick (:nick bot)
          sock (:sock bot)
          user-string (format "USER %s %s %s :%s" nick nick nick nick)
          nick-string (format "NICK %s" nick)]
      (write-line sock nick-string)
      (write-line sock user-string)
      bot))
(defn say
    "Sends PRIVMSG to person or channel"
    [bot msg channel]
    (write-line (:sock bot) (format "PRIVMSG %s :%s" channel msg)))
(defn join
    "Joins a channel"
    [bot channel]
    (write-line (:sock bot) (format "JOIN %s" channel)))
(defn part
    "leaves a channel"
    [bot channel]
    (write-line (:sock bot) (format "PART %s" channel)))
(defn quit
    "quits the network and closes the socket"
    [bot]
    ((write-line (:sock bot) "QUIT")
        (close-socket (:sock bot))))

(defn start-bot
    "Identies the bot to the network and joins any channels needed"
    [bot lines channels fns]
    (identify bot)
    (read-lines bot lines)
    (loop [line (<!! lines)]
           (if (.contains line "376")
               (apply #(join bot %) channels)
               (doseq [fn fns]
                      (fn bot line)))
           (recur (<!! lines))))
               
