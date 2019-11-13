(ns flea-game.screens.victory-2
  (:require [flea-game.button :as button]
            [flea-game.music :as music]
            [flea-game.screens.level-3 :as level-3]
            [flea-game.utils :as u]
            [quil.core :as q]))

(defn level-3
  [state]
  (when (:use-sound state)
    (music/switch-track :level-3))
  (-> state
      (assoc :screen :level-3
             :current-level :level-3)
      level-3/init))

(defn exit
  [state]
  (music/stop)
  (q/exit))

(def buttons [{:text        "NEXT"
               :handler     level-3
               :is-pressed? false}
              {:text        "EXIT"
               :handler     exit
               :is-pressed? false}])

(defn update-state
  [state]
  state)

(defn draw
  [state]
  (q/background 0)
  (q/text-align :center :center)
  (q/text-font (q/create-font "Courier" 30))
  (q/no-stroke)
  (doall (map (partial button/draw-button state (count buttons))
              (range)
              buttons)))

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
               (if (u/inside e (button/get-bounds i w h n))
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
