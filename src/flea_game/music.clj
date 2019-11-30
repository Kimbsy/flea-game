(ns flea-game.music
  (:require [clj-audio.core :as c]
            [clj-audio.sampled :as s]
            [clojure.java.io :as io])
  (:import javax.sound.sampled.SourceDataLine))

;;;;;;;;;;
;;;; Needed to duplicate some of the clj-audio lib so I can have sound
;;;; effects play on top of music without setting *playing* to false
;;;; and stopping both at the end of the sound effect.
;;;;;;;;;;

(def default-buffer-size (* 64 1024))

(def  ^:dynamic  *line-buffer-size*
  "Line buffer size in bytes, must correspond to an integral number of
  frames."
  default-buffer-size)

(def  ^:dynamic  *playback-buffer-size*
  "Playback buffer size in bytes."
  default-buffer-size)

(def ^:dynamic *playing2* (ref false))

(defn play2*
  "Write the given audio stream bytes to the given source data line
  using a buffer of the given size. Returns the number of bytes played."
  [#^SourceDataLine source audio-stream buffer-size]
  (let [buffer (byte-array buffer-size)]
    (dosync (ref-set *playing2* true))
    (loop [cnt 0 total 0]
      (if (and (> cnt -1) @*playing2*)
        (do
          (when (> cnt 0)
            (.write source buffer 0 cnt))
          (recur (.read audio-stream buffer 0 (alength buffer))
                 (+ total cnt)))
        (dosync
          (ref-set *playing2* false)
          total)))))

(defn play2
  "Play the given audio stream. Accepts an optional listener function
  that will be called when a line event is raised, taking the event
  type, the line and the stream position as arguments. Returns the
  number of bytes played."
  [audio-stream & [listener]]
  (let [line (s/make-line :output
                        (s/->format audio-stream)
                        *line-buffer-size*)
        p #(s/with-data-line [source line]
             (play2* source audio-stream *playback-buffer-size*))]
    (if listener
      (s/with-line-listener line listener (p))
      (p))))

;;;;;;;;;;;;;;;;;


(def tracks {:title "music/Lively Lumpsucker.mp3"
             :level "music/Fig Leaf Times Two.mp3"
             :final-level "music/Wagon Wheel.mp3"})

(def sound-effects {:whip-1 "sound-effects/whip-crack-01.wav"})

(defn init
  []
  (future (-> (:title tracks)
              io/resource
              c/->stream
              c/decode
              c/play)))

(defn switch-track
  [track-key]
  (c/stop)
  (Thread/sleep 100)
  (future (-> (track-key tracks)
              io/resource
              c/->stream
              c/decode
              c/play)))

(defn play-sound-effect
  [sound-effect-key]
  (future (-> (sound-effect-key sound-effects)
              io/resource
              c/->stream
              play2)))

(defn stop
  []
  (c/stop))
