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

(defn move
  "Move a single card on the board.  Codes:
  - a-f: columns 1-4
  - j-;: columns 5-8
  - q-r: freecells
  - u: all foundation piles"
  [board from-code to-code]
  (cond (and (cascade-index from-code)
             (freecell-index to-code))
        (cascade->freecell board
                           (cascade-index from-code)
                           (freecell-index to-code))
        ,,,
        :else board))

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
      (move "f" "q"))))
