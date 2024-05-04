;; 1. Use the str, vector, list, hash-map, and hash-set functions.

(str "henlo" " " "fren") ;=> "henlo fren"
(vector 1 2 3 4) ;=> [1 2 3 4]
(hash-map :a 1 :b 2) ;=> {:b 2, :a 1}
(hash-set 1 1 1 2 3) ;=> #{1 3 2}

;; 2. Write a function that takes a number and adds 100 to it.
(defn add-100 [x] (+ x 100))
(add-100 1) ;=> 101

(#(+ % 100) 1) ;=> 101

;; 3. Write a function, dec-maker, that works exactly like the inc-maker
;; except with subtraction:
; (def dec9 (dec-maker 9))
; (dec9 10) ;=> 1

(defn dec-by [n] (fn [x] (- x n)))
(let [dec9 (dec-by 9)] (dec9 10)) ;=> 1
((dec-by 2) 10) ;=> 8

;; 4. Write a function, mapset, that works like map except the return value is
;; a set:
; (mapset inc [1 1 2 2]) ;=> #{2 3}

(defn mapset [f xs] (reduce #(conj %1 (f %2)) #{} xs))
;; or this to decrease the number of iterations
(defn mapset2 [f xs] (let [xs' (set xs)] (reduce #(conj %1 (f %2)) #{} xs')))

(mapset inc [1 1 2 2]) ;=> #{3 2}
(mapset2 inc [1 1 2 2]) ;=> #{3 2}

;; or a simpler version
(defn mapset' [f xs] (set (map f xs)))
(mapset' inc [1 1 2 2]) ;=> #{3 2}

;; 5. Create a function that’s similar to symmetrize-body-parts except that it
;; has to work with weird space aliens with radial symmetry. Instead of
;; two eyes, arms, legs, and so on, they have five.

;; 6. Create a function that generalizes symmetrize-body-parts and the func-
;; tion you created in Exercise 5. The new function should take a col-
;; lection of body parts and the number of matching body parts to add.
;; If you’re completely new to Lisp languages and functional program-
;; ming, it probably won’t be obvious how to do this. If you get stuck, just
;; move on to the next chapter and revisit the problem later.

(def asym-alien-body-parts
  [{:name "head", :size 3} {:name "left-eye", :size 1}
   {:name "left-ear", :size 1} {:name "mouth", :size 1} {:name "nose", :size 1}
   {:name "neck", :size 2} {:name "left-shoulder", :size 3}
   {:name "left-upper-arm", :size 3} {:name "chest", :size 10}
   {:name "back", :size 10} {:name "left-forearm", :size 3}
   {:name "abdomen", :size 6} {:name "left-kidney", :size 1}
   {:name "left-hand", :size 2} {:name "left-knee", :size 2}
   {:name "left-thigh", :size 4} {:name "left-lower-leg", :size 3}
   {:name "left-achilles", :size 1} {:name "left-foot", :size 2}])

(ns exercises
  (:require [clojure.string :as string]))

(defn generic-symmetrize
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts n]
  (let [match-part (fn [part]
                     (set (map (fn [n]
                                 (update
                                   part
                                   :name
                                   #(string/replace % #"^left-" (str n "-"))))
                            (range n))))]
    (reduce #(into %1 (match-part %2)) [] asym-body-parts)))
(generic-symmetrize asym-alien-body-parts 5)
