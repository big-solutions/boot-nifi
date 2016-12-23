(def project 'big-solutions/boot-nifi)
(def version "0.1.0")

(set-env! :source-paths #{"src"}
          :resource-paths #{"resources"}
          :dependencies   '[[org.clojure/clojure "1.7.0"]
                            [big-solutions/boot-mvn "0.1.5"]
                            [org.apache.nifi/nifi-bootstrap "1.1.0"]
                            [org.apache.nifi/nifi-runtime "1.1.0"]
                            [me.raynes/fs "1.4.6"]
                            [boot/core "2.6.0" :scope "test"]])

(task-options!
 pom {:project     project
      :version     version
      :description "Boot tasks for Apache Nifi development"
      :url         "https://github.com/big-solutions/boot-nifi"
      :scm         {:url "https://github.com/big-solutions/boot-nifi"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask build
   "Build and install the project locally."
   []
   (comp (pom) (aot :all true) (jar) (install)))


(require '[boot-nifi.core :refer [nar-pom nar run-nifi download-nifi]])