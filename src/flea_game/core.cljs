(ns flea-game.core
  (:require [clojure.set :as s]
            [flea-game.screens.level-1 :as level-1]
            [flea-game.screens.level-2 :as level-2]
            [flea-game.screens.menu :as menu]
            [flea-game.ringmaster :as r]
            [flea-game.utils :as u]
            [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def flea-count 1000)
(def width 900)
(def height 600)

(defn ->flea
  []
  (let [pos {:x      (+ (/ width 2) (rand-int 100) -50)
             :y      (+ (/ height 2) (rand-int 100) -50)
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
  {:x      (/ width 2)
   :y      (height 2)
   :status :waiting
   :px     100
   :py     100
   :tx     150
   :ty     150
   :dj     0
   :tj     10})

(defn setup []
  (q/frame-rate 60)
  {:fleas      (take flea-count (repeatedly ->flea))
   :ringmaster (r/->ringmaster)
   :held-keys  {}
   :screen     :level-1})

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
