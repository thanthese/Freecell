(ns freecell.calc
  (:require [freecell.display :as display])
  (:require [freecell.definitions :as defs]))

(defn cascade-continuity [cascade]
  (vec (map-indexed
         (fn [i card]
           (let [up (get cascade (dec i))
                 dn (get cascade (inc i))
                 goes-up (defs/goes-on-cascade? up card)
                 goes-dn (defs/goes-on-cascade? card dn)]
             (cond (and goes-up goes-dn) (assoc card :continuity :both)
                   goes-up (assoc card :continuity :up)
                   goes-dn (assoc card :continuity :down)
                   :else card)))
         cascade)))

(defn continuity [board]
  (update-in board [:cascades] (fn [cs] (vec (map cascade-continuity cs)))))
