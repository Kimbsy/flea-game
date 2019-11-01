(ns flea-game.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(def black [0 0 0])
(def width 500)
(def height 500)

(defn ->flea
  []
  {:x (rand-int width)
   :y (rand-int height)})

(defn setup []
  (q/frame-rate 30)
  {:fleas (take 1000 (repeatedly ->flea))})

(defn rand-bool
  []
  (when (= 0 (rand-int 2))
    true))

(defn update-flea
 [f]
  (-> f
      (update :x #(+ % (- (rand-int 3) 1)))
      (update :y #(+ % (- (rand-int 3) 1)))))

(defn update-state [state]
  (update state :fleas #(map update-flea %)))

(defn draw-flea
  [{:keys [x y]}]
  (q/point x y))

(defn draw-state [state]
  (q/background 240)  
  (apply q/fill black)
  (q/stroke-weight 2)
  (doall (map draw-flea (:fleas state))))

(defn ^:export run-sketch []
  (q/defsketch flea-game
    :host "flea-game"
    :size [width height]
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [m/fun-mode]))
