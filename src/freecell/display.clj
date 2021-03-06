(ns freecell.display)

(def cascade-mobility-symbol \u263C)
(def foundation-mobility-symbol \u2605)
(def duplicate-symbol \u2630)
(def tangled-symbol \u00A7)

(def continuity-up-symbol   \u2510)
(def continuity-both-symbol \u2502)
(def continuity-down-symbol \u2518)

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
    (= continuity :up)   (str " " continuity-up-symbol)
    (= continuity :both) (str " " continuity-both-symbol)
    (= continuity :down) (str " " continuity-down-symbol)))

(defn- pretty-card [{:keys [suit
                            rank
                            continuity
                            cascade-mobile
                            foundation-mobile
                            duplicate
                            tangled] :as card}]
  (let [pad (fn [string] (str string " "))]
    (str (pretty-rank rank)
         (pretty-suit suit)
         (pretty-continuity continuity)
         (if foundation-mobile
           (pad foundation-mobility-symbol)
           (if cascade-mobile
             (pad cascade-mobility-symbol)
             "  "))
         (if duplicate
           (pad duplicate-symbol)
           (if tangled
             (pad tangled-symbol)
             "  "))
         "  ")))

(defn- pretty-cascades [cascades]
  (for [depth (range (apply max (map count cascades)))]
    (apply str (for [c cascades]
                 (pretty-card (get c depth))))))

(defn- pretty-freecells [cells]
  (map pretty-card cells))

(defn- pretty-foundations [foundations]
  (for [[suit rank] foundations]
    (pretty-card {:suit suit :rank rank})))

(defn- times-str [n string]
  (apply str (repeat n string)))

(def banner (times-str 8 (str (times-str 7 \u2500) "   ")))

(defn board
  "Pretty-print board."
  [{:keys [freecells foundations cascades]}]
  (do
    (println)
    (println (str (apply str (pretty-freecells freecells))
                  (apply str (pretty-foundations foundations))))
    (println banner)
    (doseq [line (pretty-cascades cascades)]
      (println line))
    (println)))
