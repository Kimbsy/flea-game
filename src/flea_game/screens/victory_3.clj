(ns flea-game.screens.victory-3
  (:require [flea-game.button :as button]
            [flea-game.music :as music]
            [flea-game.screens.level-4 :as level-4]
            [flea-game.utils :as u]
            [quil.core :as q]))

(defn level-4
  [state]
  (when (:use-sound state)
    (music/switch-track :final-level))
  (-> state
      (assoc :screen :level-4
             :current-level :level-4)
      level-4/init))

(defn exit
  [state]
  (music/stop)
  (q/exit))

(def buttons [{:text        "EXIT"
               :handler     exit
               :is-pressed? false}
              {:text        "NEXT"
               :handler     level-4
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
    (q/text "Bravo!" (/ w 2) (/ h 5))
    (q/text-font (get-in state [:fonts :description]))
    (q/text "That took some serious skill! Now for the finale!" (/ w 2) (/ h 3))
    (q/text "(Look out for the walls of fire!)" (/ w 2) (* 3 (/ h 7)))
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
