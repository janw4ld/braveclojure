# fwpd

guesses whether suspect is a vampire or not :)

## pre-requisites

official clojure cli (`clj`)

## usage

```bash
clj -M -m fwpd.core suspects.csv 3
```

or

```bash
clj -T:build uber
java -jar ./target/core-0.1.8-standalone.jar suspects.csv 3
```
