# freecell

Command-line freecell.  Features hint system.

## Usage from REPL

    lein repl
    (-main)

## Package and run as jar

    lein uberjar

    java -Dfile.encoding=utf-8 -jar freecell-0.1-dev-standalone.jar

Note: I've included a pre-packaged jar in the source.

## Instructions

Freecell, the best solitaire evar!

To move, enter from-to coordinate then <Enter>. Codes:

    - a-f :  cascades 1-4
    - j-; :  cascades 5-8
    - q-r :  freecells
    - u   :  all foundations
    - A   :  move all cards to foundation piles iteratively

For Example, `aq<Enter>` would move the top card from the first cascade into
the first freecell.  You can enter multiple moves at once: `aqswde<Enter>`

Double-press any code to Do What I Mean™.  For example, `aa<Enter>`.

Legend:

    - ☼ :  card could move to top of cascade
    - ★ :  card could move to foundation (will cover ☼ )
    - § :  card's parent or child is in same cascade, unconnected
    - ☰ :  multiple cards of same rank and color in cascade (will cover §)
    - ┐ :
    - │ :  card is connected to parent/child/both
    - ┘ :

Additional keys:

    - Q: quit
    - N: new game
    - R: restart game
    - ?: this help

## "Screenshot"

Note: Your browser may not render the unicode symbols/spaces correctly.  View
`screenshot.txt` for a more accurate rendering.

    9♠        J♢                             ♡        A♠        4♢        3♣
    ───────   ───────   ───────   ───────   ───────   ───────   ───────   ───────
    Q♠    §   5♣ ┐      8♡  ☼ ☰   6♢    ☰   7♣        6♠        Q♡ ┐
    A♡  ★ §   4♡ │      8♢  ☼ ☰   t♢  ☼     8♣    §   7♠        J♠ ┘
    K♢    §   3♠ ┘      5♢ ┐★     6♡    ☰   K♡ ┐      K♣ ┐
    2♠  ★ §             4♠ ┘      2♡  ☼     Q♣ │      Q♢ │
    K♠                            7♢  ☼ ☰   J♡ │      J♣ │
    5♠                            t♠        t♣ │      t♡ │☼
    9♢ ┐                          7♡ ┐☼ ☰   9♡ ┘  §   9♣ ┘
    8♠ ┘☼                         6♣ │  §
                                  5♡ │
                                  4♣ │★
                                  3♡ ┘☼

    Enter move (? for help):

## License

Copyright (C) 2011 thanthese productions

Distributed under the Eclipse Public License, the same as Clojure.
