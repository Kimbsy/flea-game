(ns flea-game.core
  (:require [flea-game.screens.level-1 :as level-1]
            [flea-game.screens.level-2 :as level-2]
            [flea-game.screens.menu :as menu]
            [flea-game.flea :as f]
            [flea-game.ringmaster :as r]
            [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def flea-count 1000)
(def width 900)
(def height 600)

(defn setup []
  (q/frame-rate 60)
  {:fleas        (take flea-count (repeatedly #(f/->flea width height)))
   :ringmaster   (r/->ringmaster)
   :held-keys    {}
   :game-running false
   :screen       :menu
   :screen-size  {:w width
                  :h height}})

(defn screen-update-state [state]
  (case (:screen state)
    :menu    (menu/update-state state)
    :level-1 (level-1/update-state state)
    :level-2 (level-2/update-state state)))

(defn screen-draw
  [state]
  (case (:screen state)
    :menu    (menu/draw state)
    :level-1 (level-1/draw state)
    :level-2 (level-2/draw state)))

(defn screen-key-pressed
  [state e]
  (case (:screen state)
    :menu    (menu/key-pressed state e)
    :level-1 (level-1/key-pressed state e)
    :level-2 (level-2/key-pressed state e)))

(defn screen-key-released
  [state e]
  (case (:screen state)
    :menu    (menu/key-released state e)
    :level-1 (level-1/key-released state e)
    :level-2 (level-2/key-released state e)))

(defn screen-mouse-pressed
  [state e]
  (case (:screen state)
    :menu    (menu/mouse-pressed state e)
    :level-1 (level-1/mouse-pressed state e)
    :level-2 (level-2/mouse-pressed state e)))

(defn screen-mouse-released
  [state e]
  (case (:screen state)
    :menu    (menu/mouse-released state e)
    :level-1 (level-1/mouse-released state e)
    :level-2 (level-2/mouse-released state e)))

(defn ^:export run-sketch []
  (q/defsketch flea-game
    :host "flea-game"
    :size [width height]
    :setup setup
    :update screen-update-state
    :draw screen-draw
    :key-pressed screen-key-pressed
    :key-released screen-key-released
    :middleware [m/fun-mode]))

(run-sketch)
