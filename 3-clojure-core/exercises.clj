(ns exercises)

(def attr partial)
((attr :hysm) {:hysm true}) ;=> true

(defn comp' [& fs] (let [fs' (reverse fs)] (fn [it] (reduce #(%2 %1) it fs'))))
((comp' #(* % 2) #(/ % 7) #(+ % 8)) 6) ;=> 4
;; (6 + 8) / 7 * 2 = 4

(defn assoc-in' [m [k & ks] v]
  (if (not ks) (assoc m k v) (assoc m k (assoc-in' (get m k {}) ks v))))
(assoc-in' {} [:hmm :yeee] 5) ;=> {:hmm {:yeee 5}}
(assoc-in' {:hmm {:yeee 3}} [:hmm :yeee] 5) ;=> {:hmm {:yeee 5}}

(defn update-in' [m ks f & args]
  (let [v (get-in m ks)
        args' (if v (into [v] args) args)]
    (assoc-in' m ks (apply f args'))))
(let [m {:yes {:true {:indeed ":|"}}}]
  (update-in' m [:yes :true :indeed] str 1 2 " mic check"))
;=> {:yes {:true {:indeed ":|12 mic check"}}}
(def m {:yes {:true {}}})
(update-in' m [:yes :true :indeed] str 1 2 " mic check")
;=> {:yes {:true {:indeed "12 mic check"}}}
(update-in m [:yes :true :indeed] str 1 2 " mic check")
;=> {:yes {:true {:indeed "12 mic check"}}}
