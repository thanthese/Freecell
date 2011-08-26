(ns freecell.core
  (:require [freecell.definitions :as defs])
  (:require [freecell.display :as display])
  (:require [freecell.calc :as calc]))

(defn -main [& args]
  (let [deck (shuffle (defs/deck))]
    (-> deck
      defs/board
      calc/calculate-annotations
      display/board)))
