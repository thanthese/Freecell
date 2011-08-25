(ns freecell.definitions)

(def suits [:club :diamond :spade :heart])
(def ranks (range 13))

(defn red? [suit]
  (or (= suit :heart)
      (= suit :diamond)))

(defn opposite-color? [suit-a suit-b]
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

(defn one-bigger? [bigger smaller]
  (= bigger (inc smaller)))

(defn goes-on-cascade?
  "'Bottom card' refers to the z-index.  Doesn't consider empty cascades"
  [bottom-card top-card]
  (and top-card
       bottom-card
       (opposite-color? (:suit top-card)
                        (:suit bottom-card))
       (one-bigger? (:rank bottom-card)
                    (:rank top-card))))

