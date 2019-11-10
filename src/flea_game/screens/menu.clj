(ns flea-game.screens.menu
  (:require [flea-game.music :as music]
            [flea-game.utils :as u]
            [quil.core :as q]))

(defn play-game
  [state]
  (music/switch-track :level-1)
  (-> state
        (assoc :screen :level-1)))

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

    (q/text-font (q/create-font  "Courier" 30))
    (q/no-stroke)
    (doall (map (partial draw-button state (count buttons))
                (range)
                buttons))))

(defn key-pressed
  [state e]
  (music/play-sound-effect :whip-1)
  state)

(defn key-released
  [state e]
  state)

(defn mouse-pressed
  [{{:keys [w h]} :screen-size :as state} e]
  (let [n (count default-buttons)]
    (doall
     (reduce (fn [state [i b]]
               (if (u/inside e (get-bounds i w h n))
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
