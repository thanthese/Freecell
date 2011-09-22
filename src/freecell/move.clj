(ns freecell.move
  (:require [freecell.definitions :as defs]))

(defn- foundation-code? [code]
  (= code "u"))

(defn- cascade-index [code]
  (get {"a" 0, "s" 1, "d" 2, "f" 3, "j" 4, "k" 5, "l" 6, ";" 7} code))

(defn- freecell-index [code]
  (get {"q" 0, "w" 1, "e" 2, "r" 3} code))

(defn- max-cards-to-move
  "Maximum number of cards that can currently be moved at one time."
  [board to-empty-cascade?]
  (let [empty-freecells (- 4 (count (filter identity (:freecells board))))
        empty-columns (count (filter empty? (:cascades board)))]
    (* (inc empty-freecells)
       (Math/pow 2 ((if to-empty-cascade? dec identity) empty-columns)))))

(defn- first-continuous-group
  "First continuous sequence of cards on cascade."
  [cascade]
  (let [pairs (partition 2 1 (reverse (conj cascade nil)))]
    (reverse
      (map second
           (take-while (fn [[top bot]]
                         (or (nil? top)
                             (defs/goes-on-cascade? bot top)))
                       pairs)))))

(defn- cascade->nil-card [board cascade]
  "Return max group of cards that can be moved from cascade to an empty
  cascade.  Takes frecell/empty column constraints into account."
  (take-last (max-cards-to-move board true)
             (first-continuous-group cascade)))

(defn- cascade->existing-card [board cascade to-card]
  "Return group of cards from top of cascade that can be played on top card.
  Takes freecell/empty column constraints into account."
  (let [max (max-cards-to-move board false)
        from-group (drop-while (fn [card]
                                 (not (defs/goes-on-cascade? to-card card)))
                               (first-continuous-group cascade))]
    (when (<= (count from-group) max)
      from-group)))

(defn- cascade->cascade [board cascade-from cascade-to]
  (let [to-card (defs/top-card (get-in board [:cascades cascade-to]))
        cascade (get-in board [:cascades cascade-from])
        from-group (if (nil? to-card)
                     (cascade->nil-card board cascade)
                     (cascade->existing-card board cascade to-card))
        size (count from-group)]
    (if (> size 0)
      (-> board
        (update-in [:cascades cascade-from] #(vec (drop-last size %)))
        (update-in [:cascades cascade-to] #(vec (concat % from-group))))
      board)))

(defn- cascade->freecell [board cascade-index freecell-index]
  (let [cascade-card (defs/top-card (get-in board [:cascades cascade-index]))
        freecell-card (get-in board [:freecells freecell-index])]
    (if (and cascade-card
             (nil? freecell-card))
      (-> board
        (update-in [:cascades cascade-index] pop)
        (assoc-in [:freecells freecell-index ] cascade-card))
      board)))

(defn- freecell->foundation [board freecell-index]
  (let [card (get-in board [:freecells freecell-index])]
    (if (defs/goes-on-foundation? (:foundations board) card)
      (-> board
        (assoc-in [:freecells freecell-index] nil)
        (assoc-in [:foundations (:suit card)] (:rank card)))
      board)))

(defn- cascade->foundation [board cascade-index]
  (let [card (defs/top-card (get-in board [:cascades cascade-index]))]
    (if (defs/goes-on-foundation? (:foundations board) card)
      (-> board
        (update-in [:cascades cascade-index] pop)
        (assoc-in [:foundations (:suit card)] (:rank card)))
      board)))

(defn- freecell->cascade [board freecell-index cascade-index]
  (let [free-card (get-in board [:freecells freecell-index])
        casc-card (defs/top-card (get-in board [:cascades cascade-index]))]
    (if (or (and free-card
                 (nil? casc-card))
            (defs/goes-on-cascade? casc-card free-card))
      (-> board
        (assoc-in [:freecells freecell-index] nil)
        (update-in [:cascades cascade-index] conj free-card))
      board)))

(defn- freecell->freecell [board freecell-from freecell-to]
  (let [fr-card (get-in board [:freecells freecell-from])
        to-card (get-in board [:freecells freecell-to])]
    (if (and fr-card
             (nil? to-card))
      (-> board
        (assoc-in [:freecells freecell-from] nil)
        (assoc-in [:freecells freecell-to] fr-card))
      board)))

(declare do-what-I-mean)

(defn move
  "Move a single card on the board.  Codes:
  - a-f: columns 1-4
  - j-;: columns 5-8
  - q-r: freecells
  - u: all foundation piles"
  [board from-code to-code]
  (let [casc-fr (cascade-index from-code)
        free-to (freecell-index to-code)
        free-fr (freecell-index from-code)
        foun-to (foundation-code? to-code)
        casc-to (cascade-index to-code)]
    (cond (= from-code to-code)
          (do-what-I-mean board from-code)
          ,
          (and casc-fr casc-to)
          (cascade->cascade board casc-fr casc-to)
          ,
          (and casc-fr free-to)
          (cascade->freecell board casc-fr free-to)
          ,
          (and casc-fr foun-to)
          (cascade->foundation board casc-fr)
          ,
          (and free-fr casc-to)
          (freecell->cascade board free-fr casc-to)
          ,
          (and free-fr free-to)
          (freecell->freecell board free-fr free-to)
          ,
          (and free-fr foun-to)
          (freecell->foundation board free-fr)
          ,
          :else board)))

(defn- do-what-I-mean
  "On a double hit, do what the user probably intended."
  [board from-code]
  (let [all-codes (remove (partial = from-code)
                          ["u"
                           "a" "s" "d" "f"
                           "j" "k" "l" ";"
                           "q" "w" "e" "r"])]
    (loop [to-codes all-codes]
      (let [to-code (first to-codes)]
        (if to-code
          (let [new-board (move board from-code to-code)]
            (if (= board new-board)
              (recur (rest to-codes))
              new-board))
          board)))))

(defn all-stars
  "Recursively move all possible cards to the foundation piles, until no
  moveable cards are left."
  [board]
  (let [all-positions ["a" "s" "d" "f"
                       "j" "k" "l" ";"
                       "q" "w" "e" "r"]]
    (loop [entry-board board]
      (let [changed-board (reduce (fn [acc-board code]
                                    (move acc-board code "u"))
                                  entry-board
                                  all-positions)]
        (if (= entry-board changed-board)
          changed-board
          (recur changed-board))))))

