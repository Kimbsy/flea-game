(ns flea-game.screens.victory-2
  (:require [flea-game.button :as button]
            [flea-game.music :as music]
            [flea-game.screens.level-3 :as level-3]
            [flea-game.utils :as u]
            [quil.core :as q]))

(defn level-3
  [state]
  (-> state
      (assoc :screen :level-3
             :current-level :level-3)
      level-3/init))

(defn exit
  [state]
  (music/stop)
  (q/exit))

(def buttons [{:text        "EXIT"
               :handler     exit
               :is-pressed? false}
              {:text        "NEXT"
               :handler     level-3
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
    (q/text "Huzzah!" (/ w 2) (/ h 5))
    (q/text-font (get-in state [:fonts :description]))
    (q/text "They loved it! Let's knock it up a notch!" (/ w 2) (/ h 3))
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
