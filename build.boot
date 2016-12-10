(def project 'boot-nifi)
(def version "0.1.0-SNAPSHOT")

(set-env! :resource-paths #{"resources" "src"}
          :source-paths   #{"test"}
          :dependencies   '[[org.clojure/clojure "RELEASE"]
                            [boot/core "RELEASE" :scope "test"]
                            [adzerk/boot-test "RELEASE" :scope "test"]
                            [onetom/boot-lein-generate "0.1.3" :scope "test"]])

(task-options!
 pom {:project     project
      :version     version
      :description "FIXME: write description"
      :url         "http://example/FIXME"
      :scm         {:url "https://github.com/yourname/boot-nifi"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask build
  "Build and install the project locally."
  []
  (comp (pom) (jar) (install)))

(require '[adzerk.boot-test :refer [test]]
         '[boot-nifi.core :refer [boot-nifi-simple
                                 boot-nifi-pre
                                 boot-nifi-post
                                 boot-nifi-pass-thru]])
(deftask idea
         "Updates project.clj for Idea to pick up dependency changes."
         []
         (require 'boot.lein)
         (let [runner (resolve 'boot.lein/generate)]
           (runner)))
