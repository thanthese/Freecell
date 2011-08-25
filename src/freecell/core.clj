(ns freecell.core
  (:require [freecell.definitions :as defs])
  (:require [freecell.display :as display])
  (:require [freecell.calc :as calc]))

(defn -main [& args]
  (display/board (calc/continuity (defs/board (shuffle (defs/deck))))))
