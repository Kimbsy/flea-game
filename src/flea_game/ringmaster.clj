(ns flea-game.ringmaster
  (:require [quil.core :as q]))

(def colors {:r [255 0 0]
             :b [0 0 0]})

(def pattern [[:r :b :r]
              [:b :b :b]
              [:r :r :r]])

(def size 5)
(def acceleration 0.6)

(defn ->ringmaster
  []
  {:x         100
   :y         100
   :vx        0
   :vy        0
   :direction :up})

(defn draw-pattern
  [p {:keys [x y]}]
  (q/stroke-weight size)
  (mapv (fn [i row]
          (mapv (fn [j c]
                  (apply q/stroke (colors c))
                  (q/point (+ x (* size (- j 1)))
                           (+ y (* size (- i 1)))))
                (range) row))
        (range) p))

(defn transpose
  [m]
  (apply map list m))

(defn draw-up
  [r]
  (draw-pattern pattern r))

(defn draw-down
  [r]
  (draw-pattern (reverse pattern) r))

(defn draw-left
  [r]
  (draw-pattern (transpose pattern) r))

(defn draw-right
  [r]
  (draw-pattern (transpose (reverse pattern)) r))

(def direction-map {:up    draw-up
                    :down  draw-down
                    :left  draw-left
                    :right draw-right})

(defn draw
  [{:keys [direction] :as r}]
  ((direction-map direction) r))

(defn update-velocity
  [r {:keys [w a s d]}]
  (-> r
      (update :vy (if w #(- % acceleration) identity))
      (update :vy (if s #(+ % acceleration) identity))
      (update :vx (if a #(- % acceleration) identity))
      (update :vx (if d #(+ % acceleration) identity))))

(defn apply-friction
  [r]
  (-> r
      (update :vx #(float (* % 0.9)))
      (update :vy #(float (* % 0.9)))))

(defn update-pos
  [{:keys [vx vy] :as r}]
  (-> r
      (update :x #(+ % vx))
      (update :y #(+ % vy))))

(defn update-ringmaster
  [r {:keys [held-keys]}]
  (-> r
      (update-velocity held-keys)
      (apply-friction)
      (update-pos)))
