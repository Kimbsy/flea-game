(ns flea-game.flea
  (:require [clojure.set :as s]
            [flea-game.utils :as u]
            [quil.core :as q]))

(defn ->flea
  [screen-width screen-height]
  (let [pos {:x      (+ (/ screen-width 2) (rand-int 100) -50)
             :y      (+ (/ screen-height 2) (rand-int 100) -50)
             :status :waiting}]
    (merge pos
           (s/rename-keys pos {:x :px
                               :y :py})
           {:tx (u/random-target-offset (:x pos))
            :ty (u/random-target-offset (:y pos))}
           {:dj 0
            :tj (u/random-jump-time)})))

(defn draw-flea
  [{:keys [x y]}]
  (q/point x y))

(defn flee-vector
  [f r]
  {:x (- (:x f) (:x r))
   :y (- (:y f) (:y r))})

(defn danger-close
  [f r]
  (and (> 70 (Math/abs (- (:x f) (:x r))))
       (> 70 (Math/abs (- (:y f) (:y r))))))

(defn calc-jump-coords
  "The mouse is close, jump away!"
  [f r]
  (let [v-flee     (flee-vector f r)
        magnitude  (- (u/random-flee-distance) (u/length v-flee))
        normalized (u/normalize v-flee)]
    {:tx (+ (:x f) (* (:x normalized) magnitude))
     :ty (+ (:y f) (* (:y normalized) magnitude))}))

(defn maybe-calc-jump-coords
  "If we have finished our jump and are ready for a new one, check if
  the mouse is too close and select a new target."
  [f r]
  (if (and (= (:x f) (:px f))
           (= (:y f) (:py f)))
    (merge f
           (if (danger-close f r)
             (calc-jump-coords f r)
             {:tx (u/random-target-offset (:x f))
              :ty (u/random-target-offset (:y f))}))
    f))

(defn update-pos
  [f]
  (let [d  (:dj f)
        dx (* d (/ (- (:tx f) (:px f)) 10))
        dy (* d (/ (- (:ty f) (:py f)) 10))]
    (-> f
        (assoc :x (+ (:px f) dx))
        (assoc :y (+ (:py f) dy)))))

(defn update-flea-status
  "Let the flea transition between waiting, jumping and landing."
  [f r]
  (let [tj-speed (max 1 (- 70 (u/length (flee-vector f r))))]
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

(defn update-all
  [fleas r]
  (map (fn [f]
         (-> f
             (update-flea-status r)
             (maybe-calc-jump-coords r)
             update-pos))
       fleas))
