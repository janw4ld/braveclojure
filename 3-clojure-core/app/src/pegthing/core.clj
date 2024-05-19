(ns pegthing.core
  (:require
   [clojure.string :as string])
  (:gen-class))

(declare successful-move prompt-move game-over prompt-rows)

;;;;
;; Create the board
;;;;
(defn tri*
  "Generates lazy sequence of triangular numbers"
  ([] (tri* 0 1))

  ([sum n] (let [new-sum (+ sum n)] (cons new-sum (lazy-seq (tri* new-sum (inc n)))))))

(def tri (tri*))

(defn triangular?
  "Is the number triangular? e.g. 1, 3, 6, 10, 15, etc"
  [n]
  (= n (last (take-while #(>= n %) tri))))

(defn row-tri "The triangular number at the end of row n" [n] (last (take n tri)))

(defn row-num
  "Returns row number the position belongs to: pos 1 in row 1,
  positions 2 and 3 in row 2, etc"
  [pos]
  (inc (count (take-while #(> pos %) tri))))

(defn in-bounds?
  "Is every position less than or equal the max position?"
  [max-pos & positions]
  (= max-pos (apply max max-pos positions)))

(defn connect
  "Form a mutual connection between two positions"
  [board max-pos pos neighbor destination]
  (if (in-bounds? max-pos neighbor destination)
    (reduce (fn [new-board [p1 p2]] (assoc-in new-board [p1 :connections p2] neighbor))
      board
      [[pos destination] [destination pos]])
    board))

(defn connect-right [board max-pos pos]
  (let [neighbor (inc pos)
        destination (inc neighbor)]
    (if-not (or (triangular? neighbor) (triangular? pos))
      (connect board max-pos pos neighbor destination)
      board)))

(defn connect-down-left [board max-pos pos]
  (let [row (row-num pos)
        neighbor (+ row pos)
        destination (+ 1 row neighbor)]
    (connect board max-pos pos neighbor destination)))

(defn connect-down-right [board max-pos pos]
  (let [row (row-num pos)
        neighbor (+ 1 row pos)
        destination (+ 2 row neighbor)]
    (connect board max-pos pos neighbor destination)))

(defn add-pos
  "Pegs the position and performs connections"
  [board max-pos pos]
  (let [pegged-board (assoc-in board [pos :pegged] true)]
    (reduce (fn [acc connect-op] (connect-op acc max-pos pos))
      pegged-board
      [connect-right connect-down-left connect-down-right])))

;; trash name lol
(defn new-board [row-count]
  (let [initial-board {:rows row-count}
        max-pos (row-tri row-count)]
    (reduce (fn [board pos] (add-pos board max-pos pos)) initial-board (range 1 (inc max-pos)))))

;;;;
;; Move pegs
;;;;
(defn pegged? "Does the position have a peg in it?" [board pos] (get-in board [pos :pegged]))

(defn valid-moves
  "Return a map of all valid moves for pos, where the key is the destination and the value is the
  jumped position"
  [board pos]
  (into
    {}
    (filter
      (fn [[destination jumped]] (and (not (pegged? board destination)) (pegged? board jumped)))
      (get-in board [pos :connections]))))

(defn valid-move?
  "Return jumped position if the move from p1 to p2 is valid, nil otherwise"
  [board p1 p2]
  ((valid-moves board p1) p2))

(defn remove-peg
  "Take the peg at given position out of the board"
  [board pos]
  (assoc-in board [pos :pegged] false))

(defn make-move
  "Move peg from p1 to p2, removing jumped peg"
  [board p1 p2]
  (let [place-peg #(assoc-in %1 [%2 :pegged] true)
        move-peg #(place-peg (remove-peg %1 %2) %3)]
    (if-let [jumped (valid-move? board p1 p2)] (move-peg (remove-peg board jumped) p1 p2))))

(defn can-move?
  "Do any of the pegged positions have valid moves?"
  [board]
  (some
    (comp not-empty (partial valid-moves board))
    (map first (filter #(get (second %) :pegged) board))))

;;;;
;; Represent board textually and print it
;;;;
(def alpha-start 97)
(def alpha-end 123)
(def letters
  (map (comp str char)
    (range ;;(dec
      alpha-start ;;)
      alpha-end)))
(def pos-chars 3)


(defn colorize
  "Apply ansi color to text"
  [text color-name]
  (let [ansi-colors (into
                      {}
                      (map (fn [[name suffix]] [name (str \u001b suffix)])
                        {:red "[31m", :green "[32m", :blue "[34m", :reset "[0m"}))]
    (str (ansi-colors color-name) text (ansi-colors :reset))))

(defn render-pos [board pos]
  (str
    (nth letters (dec pos))
    (if (get-in board [pos :pegged]) (colorize "0" :blue) (colorize "-" :red))))

(defn row-positions
  "Return all positions in the given row"
  [row-num]
  (range (inc (or (row-tri (dec row-num)) 0)) (inc (row-tri row-num))))

(defn row-padding
  "String of spaces to add to the beginning of a row to center it"
  [row-num row-count]
  (let [pad-length (/ (* (- row-count row-num) pos-chars) 2)] (apply str (repeat pad-length " "))))

(defn render-row [board row-num]
  (str
    (row-padding row-num (board :rows))
    (string/join " " (map (partial render-pos board) (row-positions row-num)))))

(defn print-board [board]
  (doseq [row-num (range 1 (inc (board :rows)))] (println (render-row board row-num))))

;;;;
;; Interaction
;;;;
(defn char->pos
  "Converts a letter string to the corresponding position number"
  [letter]
  (inc (- (int (first letter)) alpha-start)))

(defn get-input-or
  "Waits for user to enter text and hit enter, then cleans the input"
  ([] (get-input-or ""))

  ([default]
   (let [input (string/trim (read-line))] (if (empty? input) default (string/lower-case input)))))

(defn str->chars
  "Given a string, return a collection consisting of each individual
  character"
  [string]
  (re-seq #"[a-zA-Z]" string))

(defn prompt-move [board]
  (println "\nHere's your board:")
  (print-board board)
  (println "Move from where to where? Enter two letters:")
  (let [input (map char->pos (str->chars (get-input-or)))]
    (if-let [new-board (make-move board (first input) (second input))]
      (successful-move new-board)
      (do (println "\n!!! That was an invalid move :(\n") (recur board)))))

(defn successful-move [board] (if (can-move? board) (prompt-move board) (game-over board)))

(defn game-over [board]
  (let [remaining-pegs (count (filter :pegged (vals board)))]
    (println "Game over! You had" remaining-pegs "pegs left:")
    (print-board board)
    (println "Play again? y/n [y]")
    (if (= "y" (get-input-or "y")) (prompt-rows) (do (println "Bye!") (System/exit 0)))))

(defn prompt-empty-peg [board]
  (println "Here's your board:")
  (print-board board)
  (println "Remove which peg? [e]")
  ;; fixed a bug where row=4, peg=e results in a soft lock :)
  (prompt-move (successful-move (remove-peg board (char->pos (get-input-or "e"))))))

(defn prompt-rows []
  (println "How many rows? [5]")
  (let [rows (Integer. (get-input-or 5)) board (new-board rows)] (prompt-empty-peg board)))

(defn -main [& _] (println "Get ready to play peg thing!") (prompt-rows))
