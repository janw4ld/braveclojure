; you can `:w !clj` to run this file in [n]vim with no plugins

(map #(+ % 2) [1 2 3 4]) ; (3 4 5 6)

1
"a string"
["a" "vector" "of" "strings"]

(+ 1 2 3)

(str "It was the panda " "all along")

(if true
  (do (println "Success!") "By Zues's hammer!")
  (do (println "Failure!") "By Aquaman's trident"))

(when true (println "Success!") "abra cadabra") ; no `do` but no else branch

(= 1 1) ; true
(= nil nil) ; true
(= 1 2) ; false

; bool operators return the values themselves, similar to python
(or false nil :large_i_mean_venti :why_cant_i_say_large) ; :large_i_mean_venti
; returns the first truthy value or the last value
(or false nil) ; nil

(and :free_wifi :hot_coffee) ; => :hot_coffee
; returns the first falsey or the last value
(and :free_wifi nil) ; nil

(def failed-protagonist-names
  ["Larry Potter" "Doreen the Explorer" "The Incredible Bulk"])

failed-protagonist-names

(defn error-message [severity]
  (str
   "OH GOD IT'S A DISTASTER! WE'RE "
   (if (= severity :mild)
     "MILDLY INCONVENIENCED"
     "ALL DOOMED")))

(error-message :mild)

{}
{:first-name "Charlie"
 :last-name "McFishwich"}

{"string-key" +}

{:name {:first "John" :middle "Appleseed" :last "Doe"}
 :age 42}

; can be constructed with `hash-map` function
(hash-map :a 1 :b 2 :c 3)  ; {:c 3, :b 2, :a 1}

(get {:name "Hysm El System"} :name) ; "Hysm El System"
(get {:age 37} :name "John Doe") ; "John Doe"

; builtin for digging into nested maps
(get-in {:a 0 :b {:c "d"}} [:b :c]) ; 1

; maps can be applied as functions
({:name "the human coffepot"} :name) ; "The human coffepot"

; keywords are :these :things :with_colons, they can be applied as functions
(:name {:name "mazen mazen"}) ; "mazen mazen"

; 🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔🤔

; keywords can have default values too
(:d {:a 1 :b 2} 99) ; 99

; vectors, 0-indexed and can be of mixed types
(get [1 2 3] 0) ; 1
(get [1 {:name "mazen"} 3] 1) ; {:name "mazen"}

; can be constructed with `vector` function
(vector 1 2 3) ; [1 2 3]

; items can be added to the end with `conj`
(conj [1 2 3] 4) ; [1 2 3 4]

; linked lists, vectors but slow
'(1 2 3) ; (1 2 3)

; can be constructed with `list` function
(list 1 "two" 3) ; (1 "two" 3)

; items are added to the front with `conj`
(conj '(1 2 3) 0)  ; (0 1 2 3)

; sets
#{"hysm!" 25 :icicle} ; #{"hysm!" 25 :icicle}

(hash-set 1 2 3) ; #{1 3 2}
(set [1 2 2 3 3]) ; #{1 3 2}
(conj #{1 2} 3) ; #{1 3 2} ; unordered

(contains? #{1 2} 2) ; true
(contains? #{1 2} 3) ; false
; return booleans, unlike bool operators

; get, keywords and applying them works the same as maps
(#{"hysm!" 25 :icicle} 25) ; 25
(get #{"hysm!" 25 :icicle} 3 :not-found) ; :not-found


