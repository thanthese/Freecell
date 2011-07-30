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

(defn -main [& args]
  (display/board (board)))
