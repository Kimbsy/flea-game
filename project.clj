(defproject flea-game "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[com.googlecode.soundlibs/mp3spi "1.9.5-1"]
                 [org.clojars.beppu/clj-audio "0.3.0"]
                 [org.clojure/clojure "1.10.1"]
                 [quil "3.1.0"]]
  :aot [flea-game.core]
  :main flea-game.core)
