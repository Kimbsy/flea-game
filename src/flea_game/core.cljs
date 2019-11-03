(ns flea-game.core
  (:require [clojure.set :as s]
            [flea-game.utils :as u]
            [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def flea-count 1000)

(def black [0 0 0])
(def width 900)
(def height 600)

(defn ->flea
  []
  (let [pos {:x      (/ width 2)
             :y      (/ height 2)
             :status :waiting}]
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
   :status :waiting
   :px 100
   :py 100
   :tx 150
   :ty 150
   :dj 0
   :tj 10})

(defn setup []
  (q/frame-rate 60)
  {:fleas (take flea-count (repeatedly ->flea))})

(defn rand-bool
  []
  (when (= 0 (rand-int 2))
    true))

(defn dec-with-reset
  "Decrement a counter i by an amount n if it is greater than n,
  otherwise return new."
  [new n i]
  (if (> i n)
    (- i n)
    new))

(defn inc-with-reset
  "Increment a counter i by an amount n if it is less than max,
  otherwise return new."
  [max new n i]
  (if (< i max)
    (+ i n)
    new))

(defn flee-vector
  [f]
  {:x (- (:x f) (q/mouse-x))
   :y (- (:y f) (q/mouse-y))})

(defn update-flea-status
  "Let the flea transition between waiting, jumping and landing."
  [f]
  (let [tj-speed (max 1 (- 50 (u/length (flee-vector f))))]
    (case (:status f)
      :waiting
      (if (> tj-speed (:tj f))
        (assoc f :status :jumping)
        (update f :tj #(- % tj-speed)))

      :jumping
      (if (< 10 (:dj f))
        (assoc f :status :landing)
        (update f :dj inc))

      :landing
      (-> f (assoc :status :waiting
                   :dj 0
                   :tj (u/random-jump-time)
                   :px (:x f)
                   :py (:y f))))))

(defn danger-close
  [f]
  (and (> 50 (Math/abs (- (:x f) (q/mouse-x))))
       (> 50 (Math/abs (- (:y f) (q/mouse-y))))))

(defn calc-jump-coords
  "The mouse is close, jump away!"
  [f]
  (let [v-flee (flee-vector f)
        magnitude   (- (u/random-flee-distance) (u/length v-flee))
        normalized  (u/normalize v-flee)]
    {:tx (+ (:x f) (* (:x normalized) magnitude))
     :ty (+ (:y f) (* (:y normalized) magnitude))}))

(defn maybe-calc-jump-coords
  "If we have finished our jump and are ready for a new one, check if
  the mouse is too close and select a new target."
  [f]
  (if (and (= (:x f) (:px f))
           (= (:y f) (:py f)))
    (merge f
           (if (danger-close f)
             (calc-jump-coords f)
             {:tx (u/random-target-offset (:x f))
              :ty (u/random-target-offset (:y f))}))
    f))

(defn update-pos
  [f]
  (let [d (:dj f)
        dx (* d (/ (- (:tx f) (:px f)) 10))
        dy (* d (/ (- (:ty f) (:py f)) 10))]
    (-> f
        (assoc :x (+ (:px f) dx))
        (assoc :y (+ (:py f) dy)))))

(defn update-flea
 [f]
  ((comp update-flea-status
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
