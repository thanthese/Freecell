(ns freecell.core
  (:require [freecell.definitions :as defs])
  (:require [freecell.display :as disp])
  (:require [freecell.annotations :as anno])
  (:require [freecell.move :as move])
  (:gen-class))

(def help-msg
  "This is Freecell, the best solitaire evar!

  To move, enter from-to coordinate then <Enter>. Codes:
  - a-f :  cascades 1-4
  - j-; :  cascades 5-8
  - q-r :  freecells
  - u   :  all foundations

    For Example, aq<Enter> would move the top card from the first cascade into
    the first freecell position.  You can enter multiple moves at once:
    aqswde<Enter>

  Q : quit
  ? : this help
  ")

(defn -main [& args]
  (loop [board (defs/board (shuffle (defs/deck)))]
    (disp/board (anno/calculate-annotations board))
    (println "Enter move (? for help):")
    (let [user-command (read-line)]
      (cond (re-find #"\?" user-command) (do (println help-msg) (recur board))
            (re-find #"Q" user-command) "Have a nice day."
            :else (recur
                    (reduce (fn [acc-board [from-code to-code]]
                              (move/move acc-board
                                         (str from-code)
                                         (str to-code)))
                            board
                            (partition 2 (.replace user-command " " ""))))))))
