(ns flea-game.fire
  (:require [flea-game.utils :as u]
            [quil.core :as q]))

(defn ->fire-wall
  [x y w h]
  {:x x
   :y y
   :w w
   :h h})

(defn in-any-wall?
  [f walls]
  (some true? (map #(u/inside? f %) walls)))

(defn draw-fire-wall
  [{:keys [x y w h]}]
  (apply q/fill u/orange)
  (q/rect x y w h)
  (q/no-fill))

(defn kill-fleas
  [{:keys [fleas fire-walls] :as state}]
  (update state :fleas (partial remove #(in-any-wall? % fire-walls))))
