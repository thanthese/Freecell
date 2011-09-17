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

(defn- pretty-cascade-mobility [mobility]
  (if mobility \u2605 " "))

(defn- pretty-foundation-mobility [mobility]
  (if mobility \u263C " "))

(defn- pretty-duplicate [duplicate]
  (if duplicate \u2630 " "))

(defn- pretty-tangled [tangled]
  (if tangled \u00A7 " "))

(defn- pretty-card [{:keys [suit
                            rank
                            continuity
                            cascade-mobile
                            foundation-mobile
                            duplicate
                            tangled] :as card}]
  (str (pretty-rank rank)
       (pretty-suit suit)
       (pretty-continuity continuity)
       (pretty-cascade-mobility cascade-mobile)
       (pretty-foundation-mobility foundation-mobile)
       (pretty-duplicate duplicate)
       (pretty-tangled tangled)
       "  "))

(defn- pretty-cascades [cascades]
  (for [depth (range (apply max (map count cascades)))]
    (apply str (for [c cascades]
                 (pretty-card (get c depth))))))

(defn- pretty-freecells [cells]
  (map pretty-card cells))

(defn- pretty-foundations [foundations]
  (for [[suit rank] foundations]
    (pretty-card {:suit suit :rank rank})))

(defn board
  "Pretty-print board."
  [{:keys [freecells foundations cascades]}]
  (do
    (println)
    (println (str (apply str (pretty-freecells freecells))
                  (apply str (pretty-foundations foundations))))
    (println)
    (doseq [line (pretty-cascades cascades)]
      (println line))
    (println)))
