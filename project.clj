(defproject irc-bot "0.1.0-SNAPSHOT"
  :description "Simple IRC Bot"
  :url "https://github.com/Camelliadev/pBuddy"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.2.395"]]
  :main ^:skip-aot irc-bot.bot
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
