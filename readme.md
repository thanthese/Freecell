Command-line freecell, featuring an annotation system.

![Screenshot](https://raw.githubusercontent.com/thanthese/Freecell/master/screenshot.png "Screenshot")

# To use

If you're using [Leiningen][l] you can run it from the REPL by navigating to `<whatever>/freecell` then

    lein repl
    (-main)

or, even more simply,

    lein run

Alternately, you can run the provided jar with

    java -Dfile.encoding=utf-8 -jar freecell-0.1-dev-standalone.jar

*Note*: This program uses extended unicode characters. To test that your terminal and terminal font support them try

    cat special-chars-test.txt

and you should see `☼ ★ ☰ § ┐ │ ┘ ♠ ♡ ♢ ♣ ─`.

## Building the jar

If you fancy building the jar yourself, you'll again need [Leiningen][l]. Navigate to `<whatever>/freecell`, then

[l]: https://github.com/technomancy/leiningen

    lein uberjar

# Instructions

To move, enter from-to coordinate then `<Enter>`. Codes:

    - a-f :  cascades 1-4
    - j-; :  cascades 5-8
    - q-r :  freecells
    - u   :  all foundations
    - A   :  move all cards to foundation piles iteratively

For Example, `aq<Enter>` would move the top card from the first cascade into the first freecell. You can enter multiple moves at once: `aqswde<Enter>`

Double-press any code to Do What I Mean™. For example, `aa<Enter>`.

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
    - ?: help

# License

Copyright (C) 2011 thanthese productions

Distributed under the Eclipse Public License, the same as Clojure.
