(ns freecell.display)

(defn- pretty-rank [rank]
  (cond (= rank nil) " "
        (= rank   0) "A"
        (= rank   9) "t"
        (= rank  10) "J"
        (= rank  11) "Q"
        (= rank  12) "K"
        :else (inc rank)))

(defn- pretty-suit [suit]
  (cond (= suit nil)      " "
        (= suit :spade)   \u2660
        (= suit :heart)   \u2661
        (= suit :diamond) \u2662
        (= suit :club)    \u2663))

(defn- pretty-continuity [continuity]
  (cond
    (= continuity nil)   "  "
    (= continuity :up)   (str " " \u2510)
    (= continuity :both) (str " " \u2502)
    (= continuity :down) (str " " \u2518)))

(defn- pretty-card [{:keys [suit rank continuity] :as card}]
  (str (pretty-rank rank)
       (pretty-suit suit)
       (pretty-continuity continuity)
       "  "))

(defn- pretty-cascades [cascades]
  (map (fn [depth]
         (apply str (map (fn [cascade]
                           (pretty-card (get cascade depth)))
                         cascades)))
       (range (apply max (map count cascades)))))

(defn- pretty-cells [cells]
  (map pretty-card cells))

(defn- pretty-foundations [foundations]
  (map (fn [[suit rank]]
         (pretty-card {:suit suit :rank rank}))
       foundations))

(defn- pretty-separator []
  "- - - - - - - - - - - + - - - - - - - - - - -")

(defn board [{:keys [freecells foundations cascades]}]
  (do
    (println)
    (println (str (apply str (pretty-cells freecells))
                  (apply str (pretty-foundations foundations))))
    (println (pretty-separator))
    (doseq [line (pretty-cascades cascades)]
      (println line))
    (println)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; (board sample-board)

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
