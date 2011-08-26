(ns freecell.calc
  (:require [freecell.display :as display])
  (:require [freecell.definitions :as defs]))

(defn nil-wrap [v]
  (vec (cons nil (conj v nil))))

(defn simplify-card [{suit :suit rank :rank}]
  {:red (defs/red? suit)
   :rank rank})

(defn cascade-continuity [cascade]
  (vec (for [[bottom middle top] (partition 3 1 (nil-wrap cascade))]
         (let [goes-dn (defs/goes-on-cascade? bottom middle)
               goes-up (defs/goes-on-cascade? middle top)
               two-way (and goes-up goes-dn)]
           (cond two-way (assoc middle :continuity :both)
                 goes-up (assoc middle :continuity :up)
                 goes-dn (assoc middle :continuity :down)
                 :else middle)))))

(defn cascade-mobile [board cascade]
  (let [other-cascades (remove (partial defs/cascades=? cascade)
                               (board :cascades))
        tops (map last other-cascades)]
    (vec (for [card cascade]
           (assoc card :cascade-mobile
                  (some #(defs/goes-on-cascade? % card) tops))))))

(defn foundation-mobile [board cascade]
  (vec (for [card cascade]
         (assoc card :foundation-mobile
                (defs/goes-on-foundation? (:foundations board) card)))))

(defn duplicates [cascade]
  (let [s-cards (map simplify-card cascade)]
    (vec (for [card cascade]
           (assoc card :duplicate
                  (> (count (filter (partial = (simplify-card card))
                                    s-cards))
                     1))))))

(defn calculate-annotations [board]
  (update-in board [:cascades]
             #(vec (map (comp cascade-continuity
                              (partial cascade-mobile board)
                              (partial foundation-mobile board)
                              duplicates)
                        %))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(def sample-board
  {:freecells [nil {:suit :diamond, :rank 2} {:suit :diamond, :rank 2} {:suit :diamond, :rank 2}],
   :foundations {:heart nil, :spade 4, :diamond nil, :club nil},
   :cascades [[{:suit :spade, :rank 8} {:suit :diamond, :rank 12} {:suit :heart, :rank 10} {:continuity :up, :suit :club, :rank 12} {:continuity :down, :suit :heart, :rank 11} {:suit :heart, :rank 2} {:suit :club, :rank 7} {:suit :heart, :rank 9}]
              [{:suit :diamond, :rank 2} {:suit :club, :rank 0} {:suit :diamond, :rank 11} {:suit :club, :rank 4} {:suit :heart, :rank 12} {:suit :heart, :rank 4} {:suit :heart, :rank 0}]
              [{:suit :diamond, :rank 7} {:suit :diamond, :rank 9} {:suit :club, :rank 3} {:suit :spade, :rank 9} {:suit :club, :rank 8} {:suit :spade, :rank 10} {:suit :spade, :rank 7}]
              [{:suit :diamond, :rank 8} {:suit :heart, :rank 8} {:suit :diamond, :rank 3} {:suit :spade, :rank 0} {:suit :spade, :rank 11} {:suit :spade, :rank 6} {:suit :club, :rank 11}]
              [{:suit :club, :rank 2} {:suit :spade, :rank 12} {:suit :diamond, :rank 6} {:suit :spade, :rank 3} {:suit :heart, :rank 3} {:suit :heart, :rank 1}]
              [{:suit :heart, :rank 7} {:suit :spade, :rank 4} {:suit :diamond, :rank 5} {:suit :diamond, :rank 8} {:suit :heart, :rank 5} {:suit :spade, :rank 1} {:suit :club, :rank 6}]
              [{:suit :heart, :rank 6} {:suit :spade, :rank 2} {:suit :spade, :rank 8} {:suit :spade, :rank 5} {:suit :club, :rank 5} {:suit :diamond, :rank 0}]
              [{:suit :club, :rank 9} {:suit :diamond, :rank 4} {:suit :diamond, :rank 1} {:suit :club, :rank 10} {:suit :diamond, :rank 10} {:suit :club, :rank 1}]]})

(comment

  (display/board (calculate-annotations sample-board))

  (clojure.pprint/pprint (calculate-annotations sample-board))

  )
