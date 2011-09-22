(ns freecell.core
  (:require [freecell.definitions :as defs])
  (:require [freecell.display :as disp])
  (:require [freecell.annotations :as anno])
  (:require [freecell.move :as move])
  (:gen-class))

;;; todo
; "A" for automatic move to foundation
; "double" for "do what I mean"

(def help-msg
  (str "Freecell, the best solitaire evar!

  To move, enter from-to coordinate then <Enter>. Codes:
  - a-f :  cascades 1-4
  - j-; :  cascades 5-8
  - q-r :  freecells
  - u   :  all foundations

    For Example, aq<Enter> would move the top card from the first cascade into
    the first freecell.  You can enter multiple moves at once: aqswde<Enter>

  Legend:
  - " disp/cascade-mobility-symbol " :  card could move to top of cascade
  - " disp/foundation-mobility-symbol " :  card could move to foundation (will cover " disp/cascade-mobility-symbol " )
  - " disp/tangled-symbol " :  card's parent or child is in same cascade, unconnected
  - " disp/duplicate-symbol " :  multiple cards of same rank and color in cascade (will cover " disp/tangled-symbol ")
  - " disp/continuity-up-symbol" :
  - " disp/continuity-both-symbol" :  card is connected to parent/child/both
  - " disp/continuity-down-symbol" :

  Q: quit  |  N: new game  |  R: restart game  |  ?: this help
  "))

(defn- strip-spaces [string]
  (.replace string " " ""))

(defn -main [& args]
  (let [initial-board (defs/board (shuffle (defs/deck)))]
    (loop [board initial-board]
      (disp/board (anno/calculate-annotations board))
      (println "Enter move (? for help):")
      (let [user-command (read-line)]
        (cond (re-find #"Q" user-command) "Have a nice day."
              (re-find #"N" user-command) (-main)
              (re-find #"R" user-command) (recur initial-board)
              (re-find #"\?" user-command) (do
                                             (println help-msg)
                                             (recur board))
              :else (recur
                      (reduce (fn [acc-board [from-code to-code]]
                                (move/move acc-board
                                           (str from-code)
                                           (str to-code)))
                              board
                              (partition 2 (strip-spaces user-command)))))))))
