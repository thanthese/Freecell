(ns freecell.calc
  (:require [freecell.display :as display])
  (:require [freecell.definitions :as defs]))

(defn nil-wrap [v]
  (vec (cons nil (conj v nil))))

(defn cascade-continuity [cascade]
  (vec (map
         (fn [[bottom middle top]]
           (let [goes-dn (defs/goes-on-cascade? bottom middle)
                 goes-up (defs/goes-on-cascade? middle top)]
             (cond (and goes-up goes-dn) (assoc middle :continuity :both)
                   goes-up (assoc middle :continuity :up)
                   goes-dn (assoc middle :continuity :down)
                   :else middle)))
         (partition 3 1 (nil-wrap cascade)))))

(defn continuity [board]
  (update-in board [:cascades] (fn [cs] (vec (map cascade-continuity cs)))))
