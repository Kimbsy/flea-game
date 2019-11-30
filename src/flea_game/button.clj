(ns flea-game.button
  (:require [flea-game.utils :as u]
            [quil.core :as q]))

(defn button-get-w
  [w]
  (/ w 4))

(defn button-get-h
  [h]
  (/ h 10))

(defn button-get-y
  [w]
  (* 3 (/ w 5)))

(defn button-get-x
  [i w n]
  (- (* (inc i) (/ w (inc n)))
     (/ (button-get-w w) 2)))

(defn button-offset
  [state i]
  (if (= i (:held-button state))
    3
    0))

(defn get-bounds
  [i w h n]
  {:x (button-get-x i w n)
   :y (button-get-y h)
   :w (button-get-w w)
   :h (button-get-h h)})

(defn draw-button
  [{{:keys [w h]} :screen-size :as state} n i {:keys [text] :as b}]
  (apply q/fill u/black)
  (q/rect (+ 3 (button-get-x i w n))
          (+ 3 (button-get-y h))
          (button-get-w w)
          (button-get-h h))

  (apply q/fill u/dark-grey)
  (q/rect (+ (button-get-x i w n)
             (button-offset state i))
          (+ (button-get-y h)
             (button-offset state i))
          (button-get-w w)
          (button-get-h h))
  (apply q/fill u/white)
  (q/text text
          (+ (+ (button-get-x i w n) (/ (button-get-w w) 2))
             (button-offset state i))
          (+ (+ (button-get-y h) (/ h 20))
             (button-offset state i))))
