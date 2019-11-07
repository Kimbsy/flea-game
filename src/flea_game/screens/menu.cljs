(ns flea-game.screens.menu
  (:require [flea-game.utils :as u]
            [quil.core :as q :include-macros true]))

(def default-buttons [{:text "START"}
                      {:text "OPTIONS"}
                      {:text "EXIT"}])

(def running-buttons [{:text "CONTINUE"}
                      {:text "OPTIONS"}
                      {:text "EXIT"}])

(defn update-state
  [state]
  state)

(defn get-y
  [i h n]
  (+ (* 2 (/ h 5)) (* i (/ (* 2 (/ h 3)) (inc n)))))

(defn draw-button
  [{{:keys [w h]} :screen-size} n i {:keys [text]}]
  (apply q/fill u/black)
  (q/rect (+ 3 (/ w 3)) (+ 3 (get-y i h n)) (/ w 3) (/ h 10))
  (apply q/fill u/dark-grey)
  (q/rect (/ w 3) (get-y i h n) (/ w 3) (/ h 10))
  (apply q/fill u/white)
  (q/text text (/ w 2) (+ (get-y i h n) (/ h 20))))

(defn draw
  [{{:keys [w h]} :screen-size :as state}]
  (let [buttons (if (:game-running state)
                  running-buttons
                  default-buttons)]
    (q/background 230)

    (apply q/fill u/dark-grey)
    (q/text-align :center :center)
    (q/text-font "serif" 50)
    (q/text "Flea Herding Simulator 2019" (/ w 2) (/ h 6))

    (q/text-font "monospace" 30)
    (q/no-stroke)
    (doall (map (partial draw-button state (count buttons))
                (range)
                buttons))))

(defn key-pressed
  [state e]
  ;; @TODO temp way of getting back into the game
  (if (= :Enter (:key e))
    (-> state
        (assoc :screen :level-1))
    state))

(defn key-released
  [state e]
  state)

;; @TODO: clicking on buttons, need bounding functions and onclick
;; handlers
(defn mouse-pressed
  [state e]
  state)

(defn mouse-released
  [state e]
  state)
