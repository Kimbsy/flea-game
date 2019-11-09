(ns flea-game.screens.level-1
  (:require [flea-game.flea :as f]
            [flea-game.music :as music]
            [flea-game.ringmaster :as r]
            [flea-game.utils :as u]
            [quil.core :as q]))

(defn update-state
  [state]
  (-> state
      (update :ringmaster #(r/update-ringmaster % state))
      (update :fleas #(f/update-all % (:ringmaster state)))))

(defn draw
  [state]
  (q/background 230)

  (apply q/stroke u/black)
  (q/stroke-weight 2)
  (doall (map f/draw-flea (:fleas state)))

  (r/draw (:ringmaster state)))

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
                     (if (#{:up :down :left :right} (:key e))
                       (constantly (:key e))
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
