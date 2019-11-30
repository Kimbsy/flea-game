(ns flea-game.screens.victory-4
  (:require [flea-game.button :as button]
            [flea-game.music :as music]
            [flea-game.utils :as u]
            [quil.core :as q]))

(defn exit
  [state]
  (music/stop)
  (q/exit))

(def buttons [{:text        "EXIT"
               :handler     exit
               :is-pressed? false}])

(defn update-state
  [state]
  state)

(defn draw
  [state]
  (let [w (get-in state [:screen-size :w])
        h (get-in state [:screen-size :h])]
    (q/background 0)
    (q/image (get-in state [:images :title-card]) 0 0 w h)
    (q/text-align :center :center)
    (q/text-font (get-in state [:fonts :title]))
    (q/text "Amazing! Fantasic! Incredible!" (/ w 2) (/ h 5))
    (q/text-font (get-in state [:fonts :description]))
    (q/text "What an astounding show! People will be talking about this night for years to come." (/ w 2) (/ h 3))
    (q/text-font (get-in state [:fonts :score]))
    (q/text (format "Final level score: %d fleas saved" (:level-score state)) (/ w 2) (/ h 2))
    (q/text (format "Total game time: %ds" (:final-time state)) (/ w 2) (+ 30 (/ h 2)))
    (q/text-font (get-in state [:fonts :button]))
    (q/no-stroke)
    (doall (map (partial button/draw-button state (count buttons))
                (range)
                buttons))))

(defn key-pressed
  [state e]
  state)

(defn key-released
  [state e]
  state)

(defn mouse-pressed
  [{{:keys [w h]} :screen-size :as state} e]
  (let [n (count buttons)]
    (doall
     (reduce (fn [state [i b]]
               (if (u/inside? e (button/get-bounds i w h n))
                 (assoc state :held-button i)
                 state))
             state
             (zipmap (range)
                     buttons)))))

(defn mouse-released
  [state e]
  (if-let [i (:held-button state)]
      (let [held-button (nth buttons (:held-button state))]
        (-> ((:handler held-button) state)
            (dissoc :held-button)))
      state))
