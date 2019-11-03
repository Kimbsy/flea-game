(ns flea-game.ringmaster
  (:require [quil.core :as q :include-macros true]))

(def colors {:r [255 0 0]
             :b [0 0 0]})

(def pattern [[:r :b :r]
              [:b :b :b]
              [:r :r :r]])

(def size 5)
(def acceleration 1)

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
  [r {:keys [ArrowUp ArrowDown ArrowLeft ArrowRight]}]
  (-> r
      (update :vy (if ArrowUp #(- % acceleration) identity))
      (update :vy (if ArrowDown #(+ % acceleration) identity))
      (update :vx (if ArrowLeft #(- % acceleration) identity))
      (update :vx (if ArrowRight #(+ % acceleration) identity))))

(defn apply-friction
  [r]
  (-> r
      (update :vx #(* % 0.9))
      (update :vy #(* % 0.9))))

(defn update-pos
  [{:keys [vx vy] :as r}]
  (-> r
      (update :x #(+ % vx))
      (update :y #(+ % vy))))
