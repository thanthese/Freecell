(ns freecell.core
  (:require [freecell.definitions :as defs])
  (:require [freecell.display :as disp])
  (:require [freecell.annotations :as anno]))

;;; todo
; foundation piles don't have annotations

(defn -main [& args]
  (let [deck (shuffle (defs/deck))]
    (-> deck
      defs/board
      anno/calculate-annotations
      disp/board)))

;;; temp testing

(def sample-board (let [deck (shuffle (defs/deck))]
    (-> deck defs/board)))
