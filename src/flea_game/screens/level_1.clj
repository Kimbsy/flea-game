(ns flea-game.screens.level-1
  (:require [flea-game.flea :as f]
            [flea-game.music :as music]
            [flea-game.ringmaster :as r]
            [flea-game.utils :as u]
            [quil.core :as q]))

(def required-score 800)

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
         (filter #(u/inside % bounds))
         count)))

(defn check-victory
  [state]
  (if (or (< required-score (:level-score state))
          (and (:debug-mode state)
               (< 2 (:level-score state))))
    (assoc state :screen :victory-1)
    state))

(defn update-state
  [state]
  (-> state
      (f/update-all)
      (r/update-state)
      (assoc :level-score (get-score state))
      (check-victory)))

(defn draw
  [state]
  (q/background 230)

  (apply q/fill u/light-grey)
  (q/no-stroke)
  (let [{:keys [x y w h]} (goal-bounds (:screen-size state))]
    (q/rect x y w h))

  (apply q/stroke u/black)
  (q/stroke-weight 2)
  (doall (map f/draw-flea (:fleas state)))

  (r/draw (:ringmaster state))

  (apply q/fill u/black)
  (q/text (str (:level-score state)) 50 50))

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
  (if (not= :whipping (get-in state [:ringmaster :status]))
    (-> state
        (assoc-in [:ringmaster :status] :whipping)
        (assoc-in [:ringmaster :whip-target] {:x (q/mouse-x)
                                              :y (q/mouse-y)})
        (update-in [:ringmaster :cracks] #(conj % (r/->crack (q/mouse-x) (q/mouse-y)))))
    state))

(defn mouse-released
  [state e]
  state)
