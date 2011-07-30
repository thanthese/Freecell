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
  (cond (= suit :heart)   \u2661
        (= suit :diamond) \u2662
        (= suit :club)    \u2663
        (= suit :spade)   \u2660))

(defn- pretty-continuity [continuity]
  (cond
    (= continuity :up)   (str " " \u2518)
    (= continuity :both) (str " " \u2502)
    (= continuity :down) (str " " \u2510)
    :else "  "))

(defn- pretty-card [{:keys [suit rank continuity]}]
  (str (pretty-rank rank)
       (pretty-suit suit)
       (pretty-continuity continuity)
       "  "))

(defn- pretty-cascades [cascades]
  (let [max-cascade-length (apply max (map count cascades))]
    (map (fn [depth]
           (apply str (map (fn [cascade]
                             (pretty-card (get (vec cascade) depth)))
                           cascades)))
         (range max-cascade-length))))

(defn- pretty-cells [cells]
  (map pretty-card cells))

(defn- pretty-foundations [foundations]
  (map (fn [[suit rank]]
         (pretty-card {:suit suit :rank rank}))
       foundations))

(defn- pretty-separator []
  "- - - - - - - - - - - - - - - -")

(defn board [{:keys [cells foundations cascades]}]
  (do
    (println)
    (println "C: " (apply str (pretty-cells cells)))
    (println "F: " (apply str (pretty-foundations foundations)))
    (println (pretty-separator))
    (doseq [line (pretty-cascades cascades)]
      (println line))
    (println)))
