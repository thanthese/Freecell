(ns freecell.core
  (:require [freecell.display :as display]))

(def suits [:club :diamond :spade :heart])
(def ranks (range 13))

(defn deck []
  (shuffle (for [suit suits rank ranks]
             {:suit suit :rank rank})))

(defn board []
  {:cells []
   :foundations (zipmap suits (repeat (count suits) nil))
   :cascades (let [d (deck)]
               [(subvec d  0  7)
                (subvec d  7 14)
                (subvec d 14 21)
                (subvec d 21 28)
                (subvec d 28 34)
                (subvec d 34 40)
                (subvec d 40 46)
                (subvec d 46 52)])})

(defn red? [suit]
  (or (= suit :heart)
      (= suit :diamond)))

(defn opposite-color? [suit-a suit-b]
  (not= (red? suit-a)
        (red? suit-b)))

(defn goes-on? [top-card bottom-card]
  (and top-card bottom-card
       (opposite-color? (:suit top-card)
                        (:suit bottom-card))
       (= (:rank top-card)
          (inc (:rank bottom-card)))))

(defn cascade-continuity [cascade]
  (map-indexed
    (fn [i card]
      (let [up (get cascade (dec i))
            dn (get cascade (inc i))
            goes-up (goes-on? up card)
            goes-dn (goes-on? card dn)]
        (cond (and goes-up goes-dn) (assoc card :continuity :both)
              goes-up (assoc card :continuity :up)
              goes-dn (assoc card :continuity :down)
              :else card)))
    cascade))

(defn continuity [board]
  (update-in board [:cascades] (fn [cs] (map cascade-continuity cs))))

(defn -main [& args]
  (display/board (continuity (board))))
