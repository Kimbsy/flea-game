(ns flea-game.core
  (:gen-class)
  (:require [flea-game.flea :as f]
            [flea-game.music :as music]
            [flea-game.ringmaster :as r]
            [flea-game.screens.level-1 :as level-1]
            [flea-game.screens.level-2 :as level-2]
            [flea-game.screens.level-3 :as level-3]
            [flea-game.screens.level-4 :as level-4]
            [flea-game.screens.menu :as menu]
            [flea-game.screens.victory-1 :as victory-1]
            [flea-game.screens.victory-2 :as victory-2]
            [flea-game.screens.victory-3 :as victory-3]
            [flea-game.screens.victory-4 :as victory-4]
            [flea-game.utils :as u]
            [quil.core :as q]
            [quil.middleware :as m]))

(def width 900)
(def height 600)
(def use-sound true)

(defn load-images
  []
  {:title-card (q/load-image "images/title-card-trimmed.png")})

(defn create-fonts
  []
  {:title       (q/create-font "URW Chancery L Medium Italic" 50)
   :description (q/create-font "URW Chancery L Medium Italic" 25)
   :button      (q/create-font "Courier" 30)
   :score       (q/create-font "Courier" 25)})

(defn setup
  []
  (q/frame-rate 60)
  (when use-sound
    (music/init))

  (load-images)

  {:fleas          (take u/flea-count (repeatedly #(f/->flea (/ width 2)
                                                             (/ height 2))))
   :ringmaster     (r/->ringmaster)
   :held-keys      {}
   :game-running   false
   :screen         :menu
   :current-level  :level-1
   :victory?       false
   :victory-timout 0
   :screen-size    {:w width
                    :h height}
   :use-sound      use-sound
   :images         (load-images)
   :fonts          (create-fonts)
   :start-millis   (System/currentTimeMillis)
   :debug-mode     false})

(defn screen-update-state
  [state]
  (case (:screen state)
    :menu      (menu/update-state state)
    :level-1   (level-1/update-state state)
    :level-2   (level-2/update-state state)
    :level-3   (level-3/update-state state)
    :level-4   (level-4/update-state state)
    :victory-1 (victory-1/update-state state)
    :victory-2 (victory-2/update-state state)
    :victory-3 (victory-3/update-state state)
    :victory-4 (victory-4/update-state state)))

(defn screen-draw
  [state]
  (case (:screen state)
    :menu      (menu/draw state)
    :level-1   (level-1/draw state)
    :level-2   (level-2/draw state)
    :level-3   (level-3/draw state)
    :level-4   (level-4/draw state)
    :victory-1 (victory-1/draw state)
    :victory-2 (victory-2/draw state)
    :victory-3 (victory-3/draw state)
    :victory-4 (victory-4/draw state)))

(defn screen-key-pressed
  [state e]
  ;; Preventing esc from closing the sketch by setting current key to 0.
  (if (= 27 (q/key-code)) ;; escape
    (set! (.key (quil.applet/current-applet)) (char 0)))

  (case (:screen state)
    :menu      (menu/key-pressed state e)
    :level-1   (level-1/key-pressed state e)
    :level-2   (level-2/key-pressed state e)
    :level-3   (level-3/key-pressed state e)
    :level-4   (level-4/key-pressed state e)
    :victory-1 (victory-1/key-pressed state e)
    :victory-2 (victory-2/key-pressed state e)
    :victory-3 (victory-3/key-pressed state e)
    :victory-4 (victory-4/key-pressed state e)))

(defn screen-key-released
  [state e]
  (case (:screen state)
    :menu      (menu/key-released state e)
    :level-1   (level-1/key-released state e)
    :level-2   (level-2/key-released state e)
    :level-3   (level-3/key-released state e)
    :level-4   (level-4/key-released state e)
    :victory-1 (victory-1/key-released state e)
    :victory-2 (victory-2/key-released state e)
    :victory-3 (victory-3/key-released state e)
    :victory-4 (victory-4/key-released state e)))

(defn screen-mouse-pressed
  [state e]
  (case (:screen state)
    :menu      (menu/mouse-pressed state e)
    :level-1   (level-1/mouse-pressed state e)
    :level-2   (level-2/mouse-pressed state e)
    :level-3   (level-3/mouse-pressed state e)
    :level-4   (level-4/mouse-pressed state e)
    :victory-1 (victory-1/mouse-pressed state e)
    :victory-2 (victory-2/mouse-pressed state e)
    :victory-3 (victory-3/mouse-pressed state e)
    :victory-4 (victory-4/mouse-pressed state e)))

(defn screen-mouse-released
  [state e]
  (case (:screen state)
    :menu      (menu/mouse-released state e)
    :level-1   (level-1/mouse-released state e)
    :level-2   (level-2/mouse-released state e)
    :level-3   (level-3/mouse-released state e)
    :level-4   (level-4/mouse-released state e)
    :victory-1 (victory-1/mouse-released state e)
    :victory-2 (victory-2/mouse-released state e)
    :victory-3 (victory-3/mouse-released state e)
    :victory-4 (victory-4/mouse-released state e)))

(defn full-exit
  [state]
  (music/stop)
  (System/exit 0))

(defn -main
  [& args]
  (q/sketch
   :host "flea-game"
   :size [width height]
   :setup setup
   :update screen-update-state
   :draw screen-draw
   :key-pressed screen-key-pressed
   :key-released screen-key-released
   :mouse-pressed screen-mouse-pressed
   :mouse-released screen-mouse-released
   :on-close full-exit
   :middleware [m/fun-mode]))
