(ns freecell.move
  (:require [freecell.definitions :as defs]))

(defn- foundation-code? [code]
  (= code "u"))

(defn- cascade-index [code]
  (get {"a" 0, "s" 1, "d" 2, "f" 3, "j" 4, "k" 5, "l" 6, ";" 7} code))

(defn- freecell-index [code]
  (get {"q" 0, "w" 1, "e" 2, "r" 3} code))

(defn- max-cards-to-move
  "Maximum number of cards that can currently be moved at one time.
  TODO: take empty cascades into account."
  [board to-cascade-index]
  (inc (- 4 (count (filter identity (:freecells board))))))

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

(defn- first-continuous-cascade-to
  "Continuous cascade down to what would play on the given card.  Solves the
  problem of 'If I want to move this cascade to that card, which cards will I
  have to move?'"
  [cascade to-card]
  (drop-while (fn [card]
                (not (or (nil? to-card)
                         (defs/goes-on-cascade? to-card card))))
              (first-continuous-group cascade)))

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

(defn- cascade->cascade [board cascade-from cascade-to]
  (let [to-card (defs/top-card (get-in board [:cascades cascade-to]))
        max (max-cards-to-move board cascade-to)
        basic-from-group (first-continuous-cascade-to
                     (get-in board [:cascades cascade-from])
                     to-card)
        from-group (if (nil? to-card)
                     (take-last max basic-from-group)
                     basic-from-group)
        size (count from-group)]
    (if (and (> size 0)
             (<= size max))
      (-> board
        (update-in [:cascades cascade-from] #(vec (drop-last size %)))
        (update-in [:cascades cascade-to] (fn [casc]
                                            (vec (concat casc from-group)))))
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
    (cond (and casc-fr casc-to)
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

;;; tests maps

(def sample-board (-> (defs/board (shuffle (defs/deck)))
                    (assoc-in [:cascades 0]
                            [{:suit :spade, :rank 9}
                             {:suit :heart, :rank 8}
                             {:suit :spade, :rank 7}
                             {:suit :heart, :rank 6}
                             {:suit :spade, :rank 5}
                             {:suit :heart, :rank 4}
                             {:suit :spade, :rank 3}])
                    (assoc-in [:cascades 1]
                            [])))

; (freecell.display/board
;   (freecell.annotations/calculate-annotations
;     (move sample-board "a" "s")))
