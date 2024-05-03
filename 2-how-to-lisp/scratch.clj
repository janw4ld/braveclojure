;; spell-checker: disable

(ns scratch
  (:require [clojure.string :as string]))

;; higher order w keda
(or + -) ; #function[clojure.core/+]

((or + -) 1 2 4) ; 7

("test" 1 2 3 4) ;# a string is not a function
; (err) class java.lang.String cannot be cast to class clojure.lang.IFn

(defn henlo "greets a fren" [name] (str "henlo " name " :)")) ; #'user/henlo
(henlo "ward") ; "henlo ward :)"

(defn overloaded-greet
  "greets one or two frens"
  ([name] (str "henlo " name " :)"))
  ([name1 name2] (str "henlo " name1 " and " name2 " :)")))
(overloaded-greet "hysm" "mazen") ; "henlo hysm and mazen :)"

(defn greet-multi
  "greets multiple frens"
  [& names]
  (str "henlo " (string/join ", " names) " :)"))
(greet-multi "hysm" "mazen" "ward") ; "henlo hysm, mazen, ward :)"

;; destructuring a vector
(defn chooser
  [[first-choice second-choice & unimportant-choices]]
  (println (str "Your first choice is: " first-choice))
  (println (str "Your second choice is: " second-choice))
  (println (str "We're ignoring the rest of your choices. "
                "Here they are in case you need to cry over them: "
                (string/join ", " unimportant-choices))))
(chooser ["Marmalade" "Handsome Jack" "Pigpen" "Aquaman"])

;; destructuring a map
(defn announce-treasure-location
  [{lat :lat, lng :lng}]
  (println (str "Treasure lat: " lat))
  (println (str "Treasure lng: " lng)))
(announce-treasure-location {:lat 1.0, :lng 2.0})

;; shorthand syntax
(defn announce-treasure-location2
  [{:keys [lat], :as treasure-location}]
  (println (str "Treasure lat: " lat))
  (println (str "Full coords " treasure-location)))
(announce-treasure-location2 {:lat 50, :lng 83})

;; implicit return of the last form
(defn trash
  []
  (+ 1 419) ;# unused value
  (- 100 1) ;# unused value
  "hysm!")
(trash) ; "hysm!"

(defn inc-by [n] #(+ % n))
((inc-by 10) 5)

(def asym-hobbit-body-parts
  [{:name "head", :size 3} {:name "left-eye", :size 1}
   {:name "left-ear", :size 1} {:name "mouth", :size 1} {:name "nose", :size 1}
   {:name "neck", :size 2} {:name "left-shoulder", :size 3}
   {:name "left-upper-arm", :size 3} {:name "chest", :size 10}
   {:name "back", :size 10} {:name "left-forearm", :size 3}
   {:name "abdomen", :size 6} {:name "left-kidney", :size 1}
   {:name "left-hand", :size 2} {:name "left-knee", :size 2}
   {:name "left-thigh", :size 4} {:name "left-lower-leg", :size 3}
   {:name "left-achilles", :size 1} {:name "left-foot", :size 2}])


(defn matching-part
  [part]
  {:name (string/replace (:name part) #"^left-" "right-"), :size (:size part)})
(defn symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts]
  (loop [remaining-asym-parts asym-body-parts
         final-body-parts []]
    (if (empty? remaining-asym-parts)
      final-body-parts
      (let [[part & remaining] remaining-asym-parts]
        (recur remaining
               (into final-body-parts (set [part (matching-part part)])))))))
(symmetrize-body-parts asym-hobbit-body-parts)

;; local bindings
(def xs [1 2 3 4])
(let [[first _ & rest] xs] [first rest]) ;=> [1 (3 4)]

(into [] (set [:a :a])) ;=> [:a]

;; detour: pattern matching
(require '[clojure.core.match :refer [match]])

(doseq [n (range 1 101)]
  (println (match [(mod n 3) (mod n 5)]
             [0 0] "FizzBuzz"
             [0 _] "Fizz"
             [_ 0] "Buzz"
             :else n)))

;; loop/recur
(defn recursive-printer
  ([] (recursive-printer 0))
  ([iteration]
   (println iteration)
   (if (>= iteration 5) (println "bye") (recursive-printer (inc iteration)))))
(recursive-printer)

(loop [iteration 0]
  (println (str "Iteration " iteration))
  (if (>= iteration 5) (println "byeee") (recur (inc iteration))))
;; loop has better performance as well

#_(this is isn't intended to be used as a comment block but life's too short)

(defn shorter-symmetrize-body-parts
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts]
  (let [match-part (fn [part]
                     {:name (string/replace (:name part) #"^left-" "right-"),
                      :size (:size part)})]
    (reduce #(into %1 (set [%2 (match-part %2)])) [] asym-body-parts)))
(shorter-symmetrize-body-parts asym-hobbit-body-parts)

(defn anon-symmetrize
  "Expects a seq of maps that have a :name and :size"
  [asym-body-parts]
  (let [match-part
          (fn [part]
            (update part :name #(string/replace % #"^left-" "right-")))]
    (reduce #(into %1 (set [%2 (match-part %2)])) [] asym-body-parts)))
(anon-symmetrize asym-hobbit-body-parts)

