(ns boot-nifi.core
  "Example tasks showing various approaches."
  {:boot/export-tasks true}
  (:require [boot.core :as boot :refer [deftask]]
            [boot.task.built-in :refer [sift target]]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [boot-mvn.core :refer [mvn]]
            [boot.util :as util]
            [me.raynes.fs :as fs]
            [me.raynes.fs.compression :as compression])
  (:import (org.apache.nifi.bootstrap RunNiFi)))

(def template
  (-> "template.nar.pom.xml"
      io/resource
      slurp))

(defn str-replace
  "Replaces all occurences of pattern ${key} with the value of the appropriate key in the replacement map"
  [template m]
  (if template
    (reduce #(string/replace %
                             (str "${" (name (first %2)) "}")
                             (str (second %2)))
            template
            (into [] m))))

(deftask nar-pom
 "Create nar pom.xml"
 [P project PROJECT sym "project id (eg. foo/bar)"
  V version VERSION str "project version"]

 (if-not project
   (do (util/fail "The P/project option is required!") (*usage*)))
 (if-not version
   (do (util/fail "The V/version option is required!") (*usage*)))

 (let [tmp (boot/tmp-dir!)
       id (name project)
       group (or (namespace project) id)]
   (fn middleware [next-handler]
     (fn handler [fileset]
       (boot/empty-dir! tmp)
       (let [nar-pom-contents (-> template
                               (str-replace {:group   group
                                             :id      id
                                             :version version}))
             nar-pom-file (io/file tmp "pom.xml")]
         (spit nar-pom-file nar-pom-contents)

       (next-handler (-> fileset
                         (boot/add-resource tmp)
                         boot/commit!)))))))

(deftask nar
 "Builds a nar file"
 [P project PROJECT sym "project id (eg. foo/bar)"
  V version VERSION str "project version"]

 (if-not project
   (do (util/fail "The P/project option is required!") (*usage*)))
 (if-not version
   (do (util/fail "The V/version option is required!") (*usage*)))

 (let [id (name project)
       nar-path (str id "-nar-" version ".nar")]
   (comp (nar-pom :project project :version version)
         (mvn :version "3.1.1"
              :args (str "package install"))
         (sift :move {(re-pattern (str "target/" nar-path))
                      nar-path}
               :include #{#"target"}
               :invert true))))


(deftask download-nifi
   "Sets up NiFi locally in the project"
   [F force bool "Forces the nifi-home directory to be overwritten"]
   (let [tmp (boot/tmp-dir!)]
     (comp (fn middleware [next-handler]
             (fn handler [fileset]
               (util/info "Downloading NiFi...\n")
               (when (or force (not (.exists (io/file "./nifi-home"))))
                     (boot/empty-dir! tmp)
                     (with-open [in (io/input-stream "/home/goran/projects/nifi.zip")
                                 out (io/output-stream (io/file tmp "nifi.zip"))]
                       (io/copy in out))
                     (compression/unzip (io/file tmp "nifi.zip") tmp)
                     (fs/copy-dir (io/file tmp "nifi-1.1.0")
                                  (io/file tmp "nifi-home")))
               (next-handler (-> fileset
                                 (boot/add-resource tmp)
                                 boot/commit!))))
           (sift :include #{#"nifi-home"})
           (target :dir #{"."} :no-clean true))))

(deftask run-nifi
   "Runs a NiFi server"
   [H home HOME str "NiFi home directory"
    V verbose bool "Verbose"]

   (fn middleware [next-handler]
     (fn handler [fileset]
       (let [conf-path (str (or home "./nifi-home") "/conf/bootstrap.conf")]
         (System/setProperty "org.apache.nifi.bootstrap.config.file" conf-path)
         (future (-> (new RunNiFi (io/file conf-path) (boolean verbose))
                     (.start))))
       (next-handler fileset))))

