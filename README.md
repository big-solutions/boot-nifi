# boot-nifi

A Boot plugin with tasks for clj-nifi projects.

## Usage


To use this in your project:

[![Clojars Project](https://img.shields.io/clojars/v/big-solutions/boot-nifi.svg)](https://clojars.org/big-solutions/boot-nifi)

and then require the tasks:

    (require '[boot-nifi.core :refer [nar-pom nar download-nifi run-nifi]])

## Tasks

Run `nar-pom` task if you only want to generate the pom.xml file for the nar distribution:

    Create nar pom.xml
    
    Options:
      -h, --help             Print this help info.
      -P, --project PROJECT  PROJECT sets project id (eg. foo/bar).
      -V, --version VERSION  VERSION sets project version.

e.g.

    $ boot nar-pom --project foo-group/bar-project --version 1.0.1
    
Run `nar` task if you want to build the nar file (includes `nar-pom`):

    Builds a nar file
    
    Options:
      -h, --help             Print this help info.
      -P, --project PROJECT  PROJECT sets project id (eg. foo/bar).
      -V, --version VERSION  VERSION sets project version.

e.g.

    $ boot nar --project foo-group/bar-project --version 1.0.1    
    
Run `download-nifi` task to download the Apache NiFi and set it up in the project directory:

    Sets up NiFi locally in the project
    
    Options:
      -h, --help   Print this help info.
      -F, --force  Forces the home directory to be overwritten

e.g.

    $ boot download-nifi
    $ boot download-nifi --force
    
Run `run-nifi` task to run a NiFi container which is installed in the specified home directory. The default NiFi installation
 is the in nifi-home directory as created by the `download-nifi` task:
 
    Runs a NiFi server
    
    Options:
      -h, --help       Print this help info.
      -H, --home HOME  HOME sets niFi home directory.
      -V, --verbose    Verbose
      
## Examples

Depending on your workflow you may define project specific tasks that wrap these, e.g.:

    (deftask build
             "Builds the application."
             []
             (comp (pom :project project
                        :version version)
                   (aot :all true)
                   (jar)
                   (install)
                   (nar :project project
                        :version version)
                   (target)))
    
    (deftask run
             "Runs the application in a local NiFi container"
             []
             (comp (build)
                   (sift :include #{#".*\.nar"})
                   (target :dir #{"nifi-home/lib"} :no-clean true)
                   (run-nifi)
                   (wait)))

## See also

- [clj-nifi](https://github.com/big-solutions/clj-nifi) - a Clojure DSL for Apache NiFi
- [clj-nifi-bundle] (https://github.com/big-solutions/clj-nifi) - Boot template for clj-nifi projects

## License

Copyright © 2016 Big Solutions

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
