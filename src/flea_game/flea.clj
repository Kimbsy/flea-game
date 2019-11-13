(ns flea-game.flea
  (:require [clojure.set :as s]
            [flea-game.utils :as u]
            [quil.core :as q]))

(defn ->flea
  [x y]
  (let [pos {:x      (+ x (rand-int 100) -50)
             :y      (+ y (rand-int 100) -50)
             :status :waiting}]
    (merge pos
           (s/rename-keys pos {:x :px
                               :y :py})
           {:tx (u/random-target-offset (:x pos))
            :ty (u/random-target-offset (:y pos))}
           {:dj 0
            :tj (rand-int 150)})))

(defn draw-flea
  [{:keys [x y]}]
  (q/point x y))

(defn flee-vector
  [f r]
  {:x (- (:x f) (:x r))
   :y (- (:y f) (:y r))})

(defn calc-jump-coords
  [f d]
  (let [v-flee     (flee-vector f d)
        magnitude  (- (u/random-flee-distance) (u/length v-flee))
        normalized (u/normalize v-flee)]
    {:tx (+ (:x f) (* (:x normalized) magnitude))
     :ty (+ (:y f) (* (:y normalized) magnitude))}))

(defn maybe-calc-jump-coords
  [f r]
  (if (= :waiting (:status f))
    (merge f
           (if (u/danger-close f r)
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
  [{:keys [fleas ringmaster] :as state}]
  (-> state
      (assoc :fleas (map (fn [f]
                           (-> f
                               (update-flea-status ringmaster)
                               (maybe-calc-jump-coords ringmaster)
                               update-pos))
                         fleas))))
