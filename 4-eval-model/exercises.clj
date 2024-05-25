(ns exercises)

(let [cmd (read-string "(println \"janw4ld, back to the future\")")] (eval cmd))

(let [cmd (list 'println "janw4ld, back to the future")] (eval cmd))


;; Create an infix function that takes a list like (1 + 3 * 4 - 5) and trans-
;; forms it into the lists that Clojure needs in order to correctly evaluate
;; the expression using operator precedence rules.
;; (1 + 3 * 4 - 5), needs operator precedence stack
;; input: (5 - 4 * 3 + 1)
;; operation: ()                    operators: ()
;; operation: (5)                   operators: ()
;; operation: (5)                   operators: (-)
;; operation: (4 5)                 operators: (-)
;; operation: (4 5)                 operators: (* -)
;; operation: (3 4 5)               operators: (* -)
;; operation: ((* 3 4) 5)           operators: (+ -)
;; operation: (1 (* 3 4) 5)         operators: (+ -)
;; operation: ((+ 1 (* 3 4)) 5)     operators: (-)
;; operation: (- (+ 1 (* 3 4)) 5)   operators: ()

(defn infix-to-prefix [tokens]
  (let [precedence {'+ 1, '- 1, '* 2, '/ 2}
        tokens (reverse tokens)
        apply-ops #(loop [ops %1
                          expr %2]
                     (if (empty? ops)
                       expr
                       (let [operator (first ops)
                             operands (take 2 expr)
                             ops (rest ops)
                             expr (drop 2 expr)]
                         (recur ops (conj expr (conj operands operator))))))]
    (loop [tokens tokens
           ops '()
           expr '()]
      (if (empty? tokens)
        (first (apply-ops ops expr))
        ;; (reduce #(cons %2 %1) expr top-ops))
        (let [token (first tokens)
              tokens (rest tokens)]
          (cond
            (number? token) (recur tokens ops (conj expr token))
            (list? token) (recur tokens ops (conj expr (infix-to-prefix token)))
            :else
            (let [[top-ops ops]
                  (split-with #(> (precedence %) (precedence token)) ops)]
              (recur tokens (conj ops token) (apply-ops top-ops expr)))))))))

(infix-to-prefix '(1 + 3 * 4 - 5)) ;=> (- (+ 1 (* 3 4)) 5)
(infix-to-prefix '(2 / 1 + 3 * 4 - 5 / 2)) ;=> (- (+ (/ 2 1) (* 3 4)) (/ 5 2))
(eval (infix-to-prefix '(2 / 1 + 3 * 4 - 5 / 2))) ;=> 23/2
(eval (infix-to-prefix '(1))) ;=> 1
(eval (infix-to-prefix '(1 - 5))) ;=> -4
(infix-to-prefix '((1 + 3) * 4 - 5)) ;=> (- (* (+ 1 3) 4) 5)
(infix-to-prefix '(2 / 3 * 4 - 5 / 5)) ;=> (- (* (/ 2 3) 4) (/ 5 5))
