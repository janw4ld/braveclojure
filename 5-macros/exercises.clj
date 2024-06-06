(ns exercises
  (:require
   [clojure.test :refer [deftest is run-tests]]))

(defn infix-to-clj [tokens]
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
              (list? token) (recur tokens ops (conj expr (infix-to-clj token)))
              :else
              (let [[top-ops ops]
                    (split-with #(> (precedence %) (precedence token)) ops)]
                (recur tokens (conj ops token) (apply-ops top-ops expr))))))))
    tokens))
(defmacro infix [tokens] (infix-to-clj tokens))

(deftest test-infix-expansion
  (is (= '(- (+ 1 (* 3 4)) 5) (macroexpand '(infix (1 + 3 * 4 - 5)))))
  (is (not= '(+ 1 (- (* 3 4) 5)) (macroexpand '(infix (1 + 3 * 4 - 5)))))
  (is
    (=
      '(- (+ (/ 2 1) (* 3 4)) (/ 5 2))
      (macroexpand '(infix (2 / 1 + 3 * 4 - 5 / 2)))))
  (is (= '(- (* (+ 1 3) 4) 5) (macroexpand '(infix ((1 + 3) * 4 - 5)))))
  (is (= '(- (* (/ 2 3) 4) (/ 5 5)) (macroexpand '(infix (2 / 3 * 4 - 5 / 5)))))
  (is (= '(- 5 2) (macroexpand '(infix [5 - 2])))))

  ;; breaks test definition
  ; (is (thrown? NullPointerException (macroexpand '(infix {+ 5, - 2})))))

(deftest test-infix-evaluation
  (is (= 23/2 (infix (2 / 1 + 3 * 4 - 5 / 2))))
  (is (= 1 (infix (1))))
  (is (= -4 (infix (1 - 5))))
  (is (= 1 (infix 1)))
  (is (= '1 (infix 1)))
  (is (not= 2 (infix 1)))
  (is (= + (infix +)))
  (is (not= * (infix +)))
  (is (= 0 (infix (+)))))

(run-tests) ;=> {:test 2, :pass 15, :fail 0, :error 0, :type :summary}

(macroexpand '(infix (1 + 3 * 4 - 5)))
(infix (1 + 3 * 4 - 5))

(defmacro code-critic
  [{:keys [good bad]}]
  (let [criticize-code (fn [criticism code] `(println ~criticism '~code))]
    `(do
       ~@(map #(apply criticize-code %)
           [["Great squid of Madrid, this is bad code:" bad]
            ["Sweet gorilla of Manila, this is good code:" good]]))))

(macroexpand '(code-critic {:good (1 + 1), :bad (+ 1 1)}))
(code-critic {:good (1 + 1), :bad (+ 1 1)})

(defmacro report
  [to-try]
  `(let [result# ~to-try]
     (if result#
       (println '~to-try "was successful:" result#)
       (println '~to-try "was not successful:" result#))))
(report (do (Thread/sleep 1000) (+ 1 1)))


