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

(defn- cascade-mobile-helper
  "Vector of cards (for example, a cascade) with :cascade-mobile annotations
  added.  True when card could be played to a non-empty cascade."
  [vector tops]
  (vec (for [card vector]
           (assoc card :cascade-mobile
                  (some #(defs/goes-on-cascade? % card) tops)))))

(defn- cascade-mobile
  "Cascade with :cascade-mobile annotations added.  True when card could be
  played on any non-empty cascade."
  [board cascade]
  (let [other-cascades (remove (partial defs/cascades=? cascade)
                               (board :cascades))]
    (cascade-mobile-helper cascade
                           (map defs/top-card other-cascades))))

(defn- freecell-to-cascade-mobile
  "Board with :cascade-mobile annotations added to freecells."
  [board]
  (assoc board :freecells (cascade-mobile-helper
                            (:freecells board)
                            (map defs/top-card (:cascades board)))))

(defn- foundation-mobile
  "Vector of cards (for example, a cascade) with :foundation-mobile annotations
  added.  True when card could be played to its foundation pile."
  [board vector]
  (vec (for [card vector]
         (assoc card :foundation-mobile
                (defs/goes-on-foundation? (:foundations board) card)))))

(defn- freecell-to-foundation-mobile
  "Board with :foundation-mobile annotations added to freecells."
  [board]
  (update-in board [:freecells] #(foundation-mobile board %)))

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
  (-> board (update-in [:cascades]
                       (fn [cascades]
                         (vec (map (comp continuity
                                         (partial cascade-mobile board)
                                         (partial foundation-mobile board)
                                         duplicates
                                         tangled)
                                   cascades))))
    freecell-to-cascade-mobile
    freecell-to-foundation-mobile))
