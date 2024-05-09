(ns build
  (:require
   [clojure.tools.build.api :as b]
   [clojure.string :as string]))

(def version (format "0.1.%s" (b/git-count-revs nil)))
(def class-dir "target/classes")
(defn jar-file [ns] (format "target/%s-%s.jar" (name ns) version))
(defn uber-file [ns] (format "target/%s-%s-standalone.jar" (name ns) version))
(defn ns-lib [ns-name] (symbol (string/replace-first "." "/" ns-name)))

;; delay to defer side effects (artifact downloads)
(def basis (delay (b/create-basis {:project "deps.edn"})))

(defn clean [_] (b/delete {:path "target"}))

(defn jar [{:keys [ns]}]
  (let [lib (if ns (ns-lib ns) 'fwpd/core)
        file (jar-file ns)]
    (b/write-pom
      {:class-dir class-dir, :lib lib, :version version, :basis @basis, :src-dirs ["src"]})
    (b/copy-dir {:src-dirs ["src" "resources"], :target-dir class-dir})
    (b/jar {:class-dir class-dir, :jar-file file})))

;; uber jar is parameterized by :main
;; $ clj -T:build uber # uses fwpd.core as entrypoint
;; $ clj -T:build uber :main pegthing.core
(defn uber [{:keys [main]}]
  (let [main (if main (symbol main) 'fwpd.core)
        file (uber-file main)]
    (clean nil)
    (b/copy-dir {:src-dirs ["src" "resources"], :target-dir class-dir})
    (b/compile-clj {:basis @basis, :ns-compile [main], :class-dir class-dir})
    (b/uber {:class-dir class-dir, :uber-file file, :basis @basis, :main main})))
(type 'fwpd.core)

