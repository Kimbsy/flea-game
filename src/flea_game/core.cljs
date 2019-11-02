(ns flea-game.core
  (:require [clojure.set :as s]
            [flea-game.utils :as u]
            [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def black [0 0 0])
(def width 900)
(def height 600)

(defn ->flea
  []
  (let [pos {:x (rand-int width)
             :y (rand-int height)}]
    (merge pos
           (s/rename-keys pos {:x :px
                               :y :py})
           {:tx (u/random-target-offset (:x pos))
            :ty (u/random-target-offset (:y pos))}
           {:dj 0
            :tj (u/random-jump-time)})))

(defn- test-flea
  []
  {:x 100
   :y 100
   :px 100
   :py 100
   :tx 150
   :ty 150
   :dj 0
   :tj 10})

(defn setup []
  (q/frame-rate 60)
  {:fleas (take 1000 (repeatedly ->flea))})

(defn rand-bool
  []
  (when (= 0 (rand-int 2))
    true))

(defn dec-with-reset
  "Decrement a counter if it is greater than 1, otherwise return new."
  [new i]
  (if (> i 1)
    (dec i)
    new))

(defn inc-with-reset
  "Increment a counter if it is less than max, otherwise return new."
  [max new i]
  (if (< i max)
    (inc i)
    new))

(defn move-timers
  "Move all the timers forward, reset if necessary."
  [f]
  (-> f
      ;; move the delta-jump timer forward if we are jumping
      (update :dj (if (or (= 1 (:tj f))
                          (not= 0 (:dj f)))
                    (partial inc-with-reset 10 0)
                    identity))
      ;; move the time-to-next-jump timer forward if we are not jumping
      (update :tj (if (= 0 (:dj f))
                    (partial dec-with-reset (u/random-jump-time))
                    identity))))

(defn maybe-calc-jump-coords
  [f]
  f)

(defn update-pos
  [f]
  (if (and (= (:x f) (:tx f))
           (= (:y f) (:ty f)))
    ;; jump is done, reset previous to current, set new target.
    (assoc f
           :px (:x f)
           :py (:y f)
           :tx (u/random-target-offset (:x f))
           :ty (u/random-target-offset (:y f)))
    
    ;; part way through jump, calculate offset and apply to current
    (let [d (:dj f)
          dx (* d (/ (- (:tx f) (:px f)) 10))
          dy (* d (/ (- (:ty f) (:py f)) 10))]
      (-> f
          (assoc :x (+ (:px f) dx))
          (assoc :y (+ (:py f) dy))))))

(defn update-flea
 [f]
  ((comp move-timers
         maybe-calc-jump-coords
         update-pos)
   f))

(defn update-state [state]
  (update state :fleas #(map update-flea %)))

(defn draw-flea
  [{:keys [x y]}]
  (q/point x y))

(defn draw-state [state]
  (q/background 240)
  (apply q/fill black)
  (q/stroke-weight 2)
  (doall (map draw-flea (:fleas state))))

(defn ^:export run-sketch []
  (q/defsketch flea-game
    :host "flea-game"
    :size [width height]
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [m/fun-mode]))

(run-sketch)
