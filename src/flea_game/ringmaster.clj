(ns flea-game.ringmaster
  (:require [flea-game.flea :as f]
            [flea-game.utils :as u]
            [quil.core :as q]))

(def colors {:r [255 0 0]
             :b [0 0 0]})

(def pattern [[:r :b :r]
              [:b :b :b]
              [:r :r :r]])

(def size 5)
(def acceleration 0.6)
(def whip-max-progress 7)
(def crack-max-progress 5)

(defn ->ringmaster
  []
  {:x             100
   :y             100
   :vx            0
   :vy            0
   :direction     :up
   :whip-progress 1
   :whip-target   nil
   :status        :idle
   :cracks        []})

(defn ->crack
  [x y]
  {:x        x
   :y        y
   :status   :waiting
   :progress 1})

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

(defn whip-vector
  [{:keys [x y] :as r}]
  (let [whip-target (or (:whip-target r)
                        {:x (q/mouse-x)
                         :y (q/mouse-y)})]
    {:x (- (:x whip-target) x)
     :y (- (:y whip-target) y)}))

(defn draw-whip
  [{:keys [x y whip-progress status] :as r}]
  (apply q/stroke u/black)
  (q/stroke-weight 2)
  (let [v        (whip-vector r)
        normal-v (u/normalize v)
        progress (/ whip-progress whip-max-progress)
        dx       (if (= :whipping status)
                   (+ x (* progress (:x v)))
                   (+ x (* 30 (:x normal-v))))
        dy       (if (= :whipping status)
                   (+ y (* progress (:y v)))
                   (+ y (* 30 (:y normal-v))))]
    (q/line x y dx dy)))

(defn draw-crack
  [{:keys [x y progress] :as c}]
  (apply q/stroke u/red)
  (let [max-radius 30
        shockwave-progress (- progress whip-max-progress)]
    (when (< 0 shockwave-progress)
      (let [radius (* max-radius (/ shockwave-progress crack-max-progress))]
        (q/ellipse x y radius radius)))))

(defn draw-cracks
  [r]
  (doall (map draw-crack (:cracks r))))

(defn draw
  [{:keys [direction] :as r}]
  ((direction-map direction) r)
  (draw-whip r)
  (draw-cracks r))

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

(defn update-whip
  [{:keys [status whip-progress] :as r}]
  (case status
    :idle
    (-> r
        (dissoc :whip-target))

    :whipping
    (if (= whip-max-progress whip-progress)
      (-> r
          (assoc :status :idle)
          (assoc :whip-progress 1))
      (-> r
          (update :whip-progress inc)))))

(defn update-crack
  [{:keys [progress] :as c}]
  (-> c
      (update :progress inc)
      (assoc :status
             (cond
                 (> whip-max-progress progress)
                 :waiting

                 (= whip-max-progress progress)
                 :crack!

                 (> (+ whip-max-progress crack-max-progress) progress)
                 :active

                 :else
                 :complete))))

(defn update-cracks
  [{:keys [cracks] :as r}]
  (assoc r :cracks (filter #(#{:waiting :crack! :active} (:status %))
                           (map update-crack cracks))))

(defn cracking?
  [c]
  (= :crack! (:status c)))

(defn whip-fleas
  [fleas ringmaster]
  (if (some cracking? (:cracks ringmaster))
    (let [active-cracks (filter cracking? (:cracks ringmaster))]
      (map
       (fn [f]
         (if-let [chosen-crack (first (filter #(u/danger-close f %) active-cracks))]
           (merge f
                  {:tj     0
                   :status :jumping
                   :px     (:x f)
                   :py     (:y f)}
                  (f/calc-jump-coords f chosen-crack))
           f))
       fleas))
    fleas))

(defn update-state
  [{:keys [ringmaster held-keys] :as state}]
  (let [fleas              (:fleas state)
        updated-ringmaster (-> ringmaster
                               (update-velocity held-keys)
                               (apply-friction)
                               (update-pos)
                               (update-whip)
                               (update-cracks))
        whipped-fleas      (whip-fleas fleas updated-ringmaster)]
    (-> state
        (assoc :ringmaster updated-ringmaster)
        (assoc :fleas whipped-fleas))))
