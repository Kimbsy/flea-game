(ns flea-game.screens.menu
  (:require [flea-game.button :as button]
            [flea-game.music :as music]
            [flea-game.utils :as u]
            [quil.core :as q]))

(defn play-game
  [state]
  (when (:use-sound state)
    (music/switch-track (:current-level state)))
  (-> state
        (assoc :screen (:current-level state))))

(defn options
  [state]
  state)

(defn exit
  [state]
  (music/stop)
  (q/exit))

(def default-buttons [{:text        "START"
                       :handler     play-game
                       :is-pressed? false}
                      {:text        "OPTIONS"
                       :handler     options
                       :is-pressed? false}
                      {:text        "EXIT"
                       :handler     exit
                       :is-pressed? false}])

(defn update-state
  [state]
  state)

(defn draw
  [{{:keys [w h]} :screen-size :as state}]
  (let [buttons (if (:game-running state)
                  (assoc-in default-buttons [0 :text] "CONTINUE")
                  default-buttons)]
    (q/background 230)

    (apply q/fill u/dark-grey)
    (q/text-align :center :center)
    (q/text-font (q/create-font "URW Chancery L Medium Italic" 50))
    (q/text "Working Title Flea Game" (/ w 2) (/ h 6))

    (q/text-font (q/create-font "Courier" 30))
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
  (let [n (count default-buttons)]
    (doall
     (reduce (fn [state [i b]]
               (if (u/inside e (button/get-bounds i w h n))
                 (assoc state :held-button i)
                 state))
             state
             (zipmap (range)
                     default-buttons)))))

(defn mouse-released
  [state e]
  (if-let [i (:held-button state)]
      (let [held-button (nth default-buttons (:held-button state))]
        (-> ((:handler held-button) state)
            (dissoc :held-button)))
      state))
