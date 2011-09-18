(ns freecell.definitions)

(def suits [:club :diamond :spade :heart])
(def ranks (range 13))

(defn- red? [suit]
  (or (= suit :heart)
      (= suit :diamond)))

(defn- opposite-color? [suit-a suit-b]
  (not= (red? suit-a)
        (red? suit-b)))

(defn deck []
  (for [suit suits rank ranks]
    {:suit suit :rank rank}))

(defn board [deck]
  {:freecells [nil nil nil nil]
   :foundations (zipmap suits (repeat nil))
   :cascades [(subvec deck  0  7)
              (subvec deck  7 14)
              (subvec deck 14 21)
              (subvec deck 21 28)
              (subvec deck 28 34)
              (subvec deck 34 40)
              (subvec deck 40 46)
              (subvec deck 46 52)]})

(defn top-card
  "Top card on the cascade, by z-index.  The one that can currently be moved."
  [cascade]
  (last cascade))

(defn color-card
  "A simplified card, showing only rank and color."
  [{suit :suit rank :rank}]
  {:red (red? suit)
   :rank rank})

(defn base-card
  "Base card, no annotations."
  [{suit :suit rank :rank}]
  {:suit suit :rank rank})

(defn cascades=?
  "Cascades equality.  Ignores annotations."
  [cascade-a cascade-b]
  (every? identity (map (fn [card-a card-b]
                          (= (base-card card-a)
                             (base-card card-b)))
                        cascade-a cascade-b)))

(defn- one-bigger? [bigger-rank smaller-rank]
  (= bigger-rank (inc smaller-rank)))

(defn goes-on-cascade?
  "Whether the top card can be played on the bottom card.  'Bottom card' refers
  to the z-index.  Doesn't consider empty cascades."
  [bottom-card top-card]
  (and top-card
       bottom-card
       (opposite-color? (:suit top-card)
                        (:suit bottom-card))
       (one-bigger? (:rank bottom-card)
                    (:rank top-card))))

(defn goes-on-foundation?
  "Whether the card could be played to the foundation piles."
  [foundations card]
  (let [c-rank (:rank card)]
    (when c-rank
      (let [f-rank ((:suit card) foundations)]
        (if (nil? f-rank)
          (= c-rank 0)
          (one-bigger? c-rank f-rank))))))
