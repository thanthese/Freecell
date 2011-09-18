(ns freecell.annotations
  (:require [clojure.set :as set])
  (:require [freecell.definitions :as defs]))

(defn- nil-wrap
  "Wrap vector with nil values.
  Ex: [2 3] -> [nil 2 3 nil]"
  [vector]
  (vec (cons nil (conj vector nil))))

(defn- continuity
  "Cascade with :continuity annotations added: :both, :up, and :down."
  [cascade]
  (vec (for [[bottom middle top] (partition 3 1 (nil-wrap cascade))]
         (let [goes-dn (defs/goes-on-cascade? bottom middle)
               goes-up (defs/goes-on-cascade? middle top)
               two-way (and goes-up goes-dn)]
           (cond two-way (assoc middle :continuity :both)
                 goes-up (assoc middle :continuity :up)
                 goes-dn (assoc middle :continuity :down)
                 :else middle)))))

(defn- cascade-mobile
  "Cascade with :cascade-mobile annotations added.  True when card could be
  played on any non-empty cascade."
  [board cascade]
  (let [other-cascades (remove (partial defs/cascades=? cascade)
                               (board :cascades))
        tops (map defs/top-card other-cascades)]
    (vec (for [card cascade]
           (assoc card :cascade-mobile
                  (some #(defs/goes-on-cascade? % card) tops))))))

(defn- foundation-mobile
  "Cascade with :foundation-mobile annotations added.  True when card could be
  played to its foundation pile."
  [board cascade]
  (vec (for [card cascade]
         (assoc card :foundation-mobile
                (defs/goes-on-foundation? (:foundations board) card)))))

(defn- duplicates
  "Cascade with :duplicate annotations added.  True when There are other cards
  in the same cascade with the same rank and color."
  [cascade]
  (let [s-cards (map defs/color-card cascade)]
    (vec (for [card cascade]
           (assoc card :duplicate
                  (> (count (filter (partial = (defs/color-card card))
                                    s-cards))
                     1))))))

(defn- tangled
  "Cascade with :tangled annotations added.  True when a card could play on or
  be played on a card in the same cascade that isn't an immediate neighbor."
  [cascade]
  (vec (for [[_ card _ :as neighbors] (partition 3 1 (nil-wrap cascade))]
         (let [far-aways (set/difference (set cascade) (set neighbors))]
           (assoc card :tangled
                  (some (fn [far] (or (defs/goes-on-cascade? card far)
                                      (defs/goes-on-cascade? far card)))
                        far-aways))))))

(defn calculate-annotations
  "Board with annotations added."
  [board]
  (update-in board [:cascades]
             (fn [cascades]
               (vec (map (comp continuity
                               (partial cascade-mobile board)
                               (partial foundation-mobile board)
                               duplicates
                               tangled)
                         cascades)))))
