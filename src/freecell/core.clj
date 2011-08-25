(ns freecell.core
  (:require [freecell.definitions :as defs])
  (:require [freecell.display :as display])
  (:require [freecell.calc :as calc]))

(defn -main [& args]
  (let [board (shuffle (defs/deck))]
    (do
      (display/board (defs/board board))
      (display/board (calc/continuity (defs/board board))))))
