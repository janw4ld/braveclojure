(ns fwpd.core
  (:require
   [clojure.string :as string])
  (:gen-class))

(defn parse-csv
  "Convert a CSV into rows of columns"
  [string]
  (map #(string/split % #",") (string/split string #"\n")))

(defn mapify-suspects
  "Return a seq of maps like {:name \"Edward Cullen\" :glitter-index 10}"
  [rows]
  (let [vamp-keys [:name :glitter-index]
        conversions {:name identity, :glitter-index #(Integer. %)}
        convert #((conversions %1) %2)]
    (map
      #(reduce (fn [suspect [vamp-key value]] (assoc suspect vamp-key (convert vamp-key value)))
         {}
         (map vector vamp-keys %)) ;; map here is zip-with
      rows)))

; pure entrypoint function
(defn main [suspects-file-path minimum-vampire-glitter]
  (let [glitter-threshold (Integer/parseInt minimum-vampire-glitter)
        glitter-filter (partial filter #(>= (:glitter-index %) glitter-threshold))
        suspects (mapify-suspects (parse-csv (slurp suspects-file-path)))]
    (glitter-filter suspects)))
; cli entrypoint
(defn -main [name glitter-threshold & _] (println (main name glitter-threshold)))

; (main "suspects.csv" "3")
;=> ({:name "Edward Cullen", :glitter-index 10}
;=>  {:name "Jacob Black", :glitter-index 3}
;=>  {:name "Carlisle Cullen", :glitter-index 6})

;; $ clj -M -m fwpd.core suspects.csv 3
;; ({:name Edward Cullen, :glitter-index 10} {:name Jacob Black, :glitter-index 3} {:name Carlisle
;; Cullen, :glitter-index 6})
