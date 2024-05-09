# Session 3

chapter 4 introduces 

- clojure's core functions & program structure concepts.
  - talks about the language's flavor of abstraction
  - the sequence and collection "abstractions" and a bunch of functions that
    work on them
  - introduces laziness (finally), infinite data structures, and (finally!!) an
    example of partial application

chapter 5 introduces purely functional programming concepts :)

i also parameterised the `uber` task in `app/build.clj` by :main, to allow for
using different entrypoints in the same app (not a good idea tho), this taught
me about evaluation, `symbol`, `resolve`, `quote` and other macro-ish things in
the language.
