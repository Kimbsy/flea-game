(ns flea-game.utils)

(def flea-count 1000)
(def black [0 0 0])
(def white [255 255 255])
(def red [255 0 0])
(def green [0 255 0])
(def orange [255 100 0])
(def light-grey [200 200 200])
(def dark-grey [100 100 100])

(def wasd-map {:w :up
               :s :down
               :a :left
               :d :right})

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
  (let [l (length vector)]
    (if (= 0.0 l)
      {:x 0.0 :y 0.0}
      {:x (* (/ 1 l) (:x vector))
       :y (* (/ 1 l) (:y vector))})))

(defn danger-close
  [f d]
  (and (> 60 (Math/abs (int (- (:x f) (:x d)))))
       (> 60 (Math/abs (int (- (:y f) (:y d)))))))

(defn inside?
  [pos bounds]
  (and (<= (:x bounds) (:x pos) (+ (:x bounds) (:w bounds)))
       (<= (:y bounds) (:y pos) (+ (:y bounds) (:h bounds)))))
