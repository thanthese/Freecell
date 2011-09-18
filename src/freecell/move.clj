(ns freecell.move
  (:require [freecell.definitions :as defs]))

(defn- foundation-code? [code]
  (= code "u"))

(defn- cascade-index [code]
  (get {"a" 0, "s" 1, "d" 2, "f" 3, "j" 4, "k" 5, "l" 6, ";" 7} code))

(defn- freecell-index [code]
  (get {"q" 0, "w" 1, "e" 2, "r" 3} code))

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

(defn move
  "Move a single card on the board.  Codes:
  - a-f: columns 1-4
  - j-;: columns 5-8
  - q-r: freecells
  - u: all foundation piles"
  [board from-code to-code]
  (let [cascade-from (cascade-index from-code)
        freecell-to (freecell-index to-code)
        freecell-from (freecell-index from-code)
        foundation-to (foundation-code? to-code)]
    (cond (and cascade-from
               freecell-to)
          (cascade->freecell board cascade-from freecell-to)
          ,,,
          (and freecell-from
               foundation-to)
          (freecell->foundation board freecell-from)
          ,,,
          (and cascade-from
               foundation-to)
          (cascade->foundation board cascade-from)
          ,,,
          :else board)))

;;; temp testing

; original board
(freecell.display/board freecell.core/sample-board )

; modified board
(freecell.display/board
  (freecell.annotations/calculate-annotations
    (-> freecell.core/sample-board
      (move "l" "r")
      (move "l" "e")
      (move "s" "q")
      (move "f" "q")
      (move "f" "w")
      (move "q" "u")
      (move "a" "q")
      (move "q" "u")
      (move "q" "u")
      (move "w" "u")
      (move "s" "q")
      (move "s" "u")
      (move "s" "u"))))
