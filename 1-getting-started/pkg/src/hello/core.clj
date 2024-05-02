(ns hello.core
  (:require
   [clojure.string :as string])
  (:gen-class))

(defn -main
  "I don't do much yet :)"
  [& _]
  (println
   (str "henlo " (string/lower-case "FREN"))))

