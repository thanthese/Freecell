(ns freecell.core
  (:require [freecell.definitions :as defs])
  (:require [freecell.display :as disp])
  (:require [freecell.annotations :as anno]))

(defn -main [& args]
  (let [deck (shuffle (defs/deck))]
    (-> deck
      defs/board
      anno/calculate-annotations
      disp/board)))
