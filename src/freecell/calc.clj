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

(def top-card last)

(defn top-cards [board]
  (map top-card (:cascades board)))

(defn cascade-mobile [board]
  (let [bottoms (top-cards board)]

(let [a [1 2 3 4 5]]
  (update-in a [(dec (count a))] inc))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def sample-board
  {:freecells [nil {:suit :diamond, :rank 2} {:suit :diamond, :rank 2} {:suit :diamond, :rank 2}],
   :foundations {:heart nil, :spade 4, :diamond nil, :club nil},
   :cascades [[{:suit :diamond, :rank 12} {:suit :heart, :rank 10} {:continuity :up, :suit :club, :rank 12} {:continuity :down, :suit :heart, :rank 11} {:suit :heart, :rank 2} {:suit :club, :rank 7} {:suit :heart, :rank 9}]
              [{:suit :diamond, :rank 2} {:suit :club, :rank 0} {:suit :diamond, :rank 11} {:suit :club, :rank 4} {:suit :heart, :rank 12} {:suit :heart, :rank 4} {:suit :heart, :rank 0}]
              [{:suit :diamond, :rank 7} {:suit :diamond, :rank 9} {:suit :club, :rank 3} {:suit :spade, :rank 9} {:suit :club, :rank 8} {:suit :spade, :rank 10} {:suit :spade, :rank 7}]
              [{:suit :heart, :rank 8} {:suit :diamond, :rank 3} {:suit :spade, :rank 0} {:suit :spade, :rank 11} {:suit :spade, :rank 6} {:suit :club, :rank 11}]
              [{:suit :club, :rank 2} {:suit :spade, :rank 12} {:suit :diamond, :rank 6} {:suit :spade, :rank 3} {:suit :heart, :rank 3} {:suit :heart, :rank 1}]
              [{:suit :heart, :rank 7} {:suit :spade, :rank 4} {:suit :diamond, :rank 5} {:suit :diamond, :rank 8} {:suit :heart, :rank 5} {:suit :spade, :rank 1} {:suit :club, :rank 6}]
              [{:suit :heart, :rank 6} {:suit :spade, :rank 2} {:suit :spade, :rank 8} {:suit :spade, :rank 5} {:suit :club, :rank 5} {:suit :diamond, :rank 0}]
              [{:suit :club, :rank 9} {:suit :diamond, :rank 4} {:suit :diamond, :rank 1} {:suit :club, :rank 10} {:suit :diamond, :rank 10} {:suit :club, :rank 1}]]})

(display/board sample-board)
