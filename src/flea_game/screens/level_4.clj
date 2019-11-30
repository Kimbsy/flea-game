(ns flea-game.screens.level-4
  (:require [flea-game.fire :as fire]
            [flea-game.flea :as f]
            [flea-game.music :as music]
            [flea-game.ringmaster :as r]
            [flea-game.utils :as u]
            [quil.core :as q]))

(def required-score 700)

(defn create-fire-walls
  [{:keys [w h]}]
  [(fire/->fire-wall (- (* 5 (/ w 7)) 10)
                     0
                     20
                     (* 3 (/ h 5)))
   (fire/->fire-wall (* 4 (/ w 7))
                     (* 2 (/ h 5))
                     20
                     (* 3 (/ h 5)))])

(defn init
  [{:keys [screen-size] :as state}]
  (-> state
      (assoc :fleas (take u/flea-count
                          (repeatedly #(f/->flea (/ (:w screen-size) 4)
                                                 (/ (:h screen-size) 2)))))
      (assoc :fire-walls (create-fire-walls screen-size))))

(defn goal-bounds
  [{:keys [w h]}]
  {:x (* 5 (/ w 7))
   :y 0
   :w (* 2 (/ w 7))
   :h h})

(defn get-score
  [{:keys [fleas screen-size]}]
  (let [bounds (goal-bounds screen-size)]
    (->> fleas
         (filter #(u/inside? % bounds))
         count)))

(defn check-victory
  [state]
  (if-not (:victory? state)
    (let [remaining-count (count (:fleas state))]
      (if (or (< required-score (:level-score state))
              (<= (- remaining-count (:level-score state)) 100)
              (and (:debug-mode state)
                   (< 2 (:level-score state))))
        (-> state
            (assoc :victory? true)
            (assoc :victory-timeout 50))
        state))
    state))

(defn advance
  [state]
  (-> state
      (assoc :screen :victory-4)
      (assoc :final-time (-> (System/currentTimeMillis)
                             (-  (:start-millis state))
                             (/ 1000)
                             int))
      (assoc :victory? false)
      (assoc-in [:ringmaster :whip-timeout] 50)
      (assoc :held-keys {})))

(defn update-state
  [state]
  (let [updated-state (-> state
                          (f/update-all)
                          (r/update-state)
                          (fire/kill-fleas)
                          (assoc :level-score (get-score state))
                          (check-victory))]
    (if (:victory? updated-state)
      (if (< 0 (:victory-timeout updated-state))
        (update updated-state :victory-timeout dec)
        (advance updated-state))
      updated-state)))

(defn draw
  [{:keys [screen-size] :as state}]
  (q/background 230)

  (apply q/fill u/light-grey)
  (q/no-stroke)
  (let [{:keys [x y w h]} (goal-bounds (:screen-size state))]
    (q/rect x y w h))

  (apply q/stroke u/black)
  (q/stroke-weight 2)
  (doall (map f/draw-flea (:fleas state)))

  (doall (map fire/draw-fire-wall (:fire-walls state)))

  (r/draw (:ringmaster state))

  (apply q/fill u/black)
  (q/text-align :right :center)
  (q/text (format "%d / %d"
                  (:level-score state)
                  (min required-score (- (count (:fleas state)) (:level-score state))))
          200 50)
  (q/text-align :center :center)

  (when (:victory? state)
    (q/no-fill)
    (apply q/stroke u/green)
    (q/stroke-weight 4)
    (q/rect 0 0 (- (:w screen-size) 2) (- (:h screen-size) 2))
    (q/stroke-weight 2)))

(defn key-pressed
  [state e]
  (if (= 27 (:key-code e)) ;; escape
    (do (when (:use-sound state) (music/switch-track :title))
        (-> state
            (assoc :screen :menu)
            (assoc :game-running true)
            (assoc :held-keys {})))
      (-> state
          (assoc-in [:held-keys (:key e)] true)
          (update-in [:ringmaster :direction]
                     (if-let [direction (u/wasd-map (:key e))]
                       (constantly direction)
                       identity)))))

(defn key-released
  [state e]
  (assoc-in state [:held-keys (:key e)] false))

(defn mouse-pressed
  [state e]
  (music/play-sound-effect :whip-1)
  (if (and (not= :whipping (get-in state [:ringmaster :status]))
           (= 0 (get-in state [:ringmaster :whip-timeout])))
    (-> state
        (assoc-in [:ringmaster :status] :whipping)
        (assoc-in [:ringmaster :whip-timeout] 50)
        (assoc-in [:ringmaster :whip-target] {:x (q/mouse-x)
                                              :y (q/mouse-y)})
        (update-in [:ringmaster :cracks] #(conj % (r/->crack (q/mouse-x) (q/mouse-y)))))
    state))

(defn mouse-released
  [state e]
  state)
