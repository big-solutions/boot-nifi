(ns boot-nifi.core
  "Example tasks showing various approaches."
  {:boot/export-tasks true}
  (:require [boot.core :as boot :refer [deftask]]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [boot.util :as util]))

(defn str-replace
  "Replaces all occurences of pattern ${key} with the value of the appropriate key in the replacement map"
  [template m]
  (if template
    (reduce #(string/replace %
                             (str "${" (name (first %2)) "}")
                             (str (second %2)))
            template
            (into [] m))))

(deftask nar
 "Create nar file"
 [P project PROJECT sym "project id (eg. foo/bar)"
  V version VERSION str "project version"]

 (if-not project
   (do (boot.util/fail "The P/project option is required!") (*usage*)))
 (if-not version
   (do (boot.util/fail "The V/version option is required!") (*usage*)))

 (let [tmp (boot/tmp-dir!)
       id (name project)
       group (or (namespace project) id)]
   (fn middleware [next-handler]
     (fn handler [fileset]
       (boot/empty-dir! tmp)
       (let [template (->> fileset
                           boot/input-files
                           (boot/by-name ["template.nar.pom.xml"])
                           first
                           boot/tmp-file
                           slurp)
             nar-pom-contents (-> template
                               (str-replace {:group   group
                                             :id      id
                                             :version version}))
             nar-pom-file (io/file tmp "pom.xml")]
         (spit nar-pom-file nar-pom-contents))
       (next-handler (-> fileset
                         (boot/add-resource tmp)
                         boot/commit!))))))
