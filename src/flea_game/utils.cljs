(ns flea-game.utils)

(defn random-jump-time
  []
  (+ 100 (rand-int 20)))

(defn random-target-offset
  [t]
  (+ t (rand-int 50) -25))
