(ns flea-game.button
  (:require [flea-game.utils :as u]
            [quil.core :as q]))

(defn button-get-x
  [w]
  (/ w 3))

(defn button-get-y
  [i h n]
  (+ (* 2 (/ h 5)) (* i (/ (* 2 (/ h 3)) (inc n)))))

(defn button-get-w
  [w]
  (/ w 3))

(defn button-get-h
  [h]
  (/ h 10))

(defn button-offset
  [state i]
  (if (= i (:held-button state))
    3
    0))

(defn get-bounds
  [i w h n]
  {:x (button-get-x w)
   :y (button-get-y i h n)
   :w (button-get-w w)
   :h (button-get-h h)})

(defn draw-button
  [{{:keys [w h]} :screen-size :as state} n i {:keys [text] :as b}]
  (apply q/fill u/black)
  (q/rect (+ 3 (button-get-x w))
          (+ 3 (button-get-y i h n))
          (button-get-w w)
          (button-get-h h))

  (apply q/fill u/dark-grey)
  (q/rect (+ (button-get-x w)
             (button-offset state i))
          (+ (button-get-y i h n)
             (button-offset state i))
          (button-get-w w)
          (button-get-h h))
  (apply q/fill u/white)
  (q/text text
          (+ (/ w 2)
             (button-offset state i))
          (+ (+ (button-get-y i h n) (/ h 20))
             (button-offset state i))))
