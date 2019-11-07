(ns flea-game.utils)

(def black [0 0 0])
(def white [255 255 255])
(def red [255 0 0])
(def light-grey [200 200 200])
(def dark-grey [100 100 100])

(def arrow-map {:ArrowUp    :up
                :ArrowDown  :down
                :ArrowLeft  :left
                :ArrowRight :right})

(defn random-jump-time
  []
  (+ 150 (rand-int 50)))

(defn random-target-offset
  [t]
  (+ t (rand-int 50) -25))

(defn random-flee-distance
  []
  (+ 70 (rand-int 10)))

(defn length
  "Determine the length of a vector."
  [vector]
  (Math/sqrt (+ (Math/pow (:x vector) 2) (Math/pow (:y vector) 2))))

(defn normalize
  "Normalize a vector to length 1."
  [vector]
  (let [length (length vector)]
    {:x (* (/ 1 length) (:x vector))
     :y (* (/ 1 length) (:y vector))}))
