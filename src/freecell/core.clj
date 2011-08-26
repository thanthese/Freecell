(ns freecell.core
  (:require [freecell.definitions :as defs])
  (:require [freecell.display :as display])
  (:require [freecell.calc :as calc]))

(defn -main [& args]
  (let [deck (shuffle (defs/deck))]
    (do
      (display/board (defs/board deck))
      (-> deck
        defs/board
        calc/continuity
        calc/cascades-mobile
        calc/foundations-mobile
        display/board))))
