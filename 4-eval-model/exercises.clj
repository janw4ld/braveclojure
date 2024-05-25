(ns exercises
  (:require
   [clojure.test :refer [deftest is run-tests]]))

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

(defn infix [tokens]
  (if (seqable? tokens)
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
              (list? token) (recur tokens ops (conj expr (infix token)))
              :else
              (let [[top-ops ops]
                    (split-with #(> (precedence %) (precedence token)) ops)]
                (recur tokens (conj ops token) (apply-ops top-ops expr))))))))
    tokens))

(deftest infix-test
  (is (= '(- (+ 1 (* 3 4)) 5) (infix '(1 + 3 * 4 - 5))))
  (is (not= '(+ 1 (- (* 3 4) 5)) (infix '(1 + 3 * 4 - 5))))
  (is (= '(- (+ (/ 2 1) (* 3 4)) (/ 5 2)) (infix '(2 / 1 + 3 * 4 - 5 / 2))))
  (is (= 23/2 (eval (infix '(2 / 1 + 3 * 4 - 5 / 2)))))
  (is (= 1 (eval (infix '(1)))))
  (is (= -4 (eval (infix '(1 - 5)))))
  (is (= '(- (* (+ 1 3) 4) 5) (infix '((1 + 3) * 4 - 5))))
  (is (= '(- (* (/ 2 3) 4) (/ 5 5)) (infix '(2 / 3 * 4 - 5 / 5))))
  (is (= '(- 5 2) (infix '[5 - 2])))
  (is (= '1 (infix '1)))
  (is (= 1 (infix '1)))
  (is (not= '2 (infix '1)))
  (is (= '+ (infix '+)))
  (is (not= '* (infix '+)))
  (is (thrown? NullPointerException (infix '{+ 5, - 2}))))
(run-tests) ;=> {:test 1, :pass 15, :fail 0, :error 0, :type :summary}
