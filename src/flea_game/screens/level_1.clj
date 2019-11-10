(ns flea-game.screens.level-1
  (:require [flea-game.flea :as f]
            [flea-game.music :as music]
            [flea-game.ringmaster :as r]
            [flea-game.utils :as u]
            [quil.core :as q]))

(defn goal-bounds
  [{:keys [w h]}]
  {:x (* 5 (/ w 7))
   :y (* 2 (/ h 5))
   :w (/ w 7)
   :h (/ h 5)})

(defn get-score
  [{:keys [fleas screen-size]}]
  (let [bounds (goal-bounds screen-size)]
    (->> fleas
         (filter #(u/inside % bounds))
         count)))

(defn update-state
  [state]
  (-> state
      (update :ringmaster #(r/update-ringmaster % state))
      (update :fleas #(f/update-all % (:ringmaster state)))
      (assoc :level-score (get-score state))))

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
  (if (= 10 (:key-code e)) ;; enter
    (do (music/switch-track :title)
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
  state)

(defn mouse-released
  [state e]
  state)
