# boot-nifi

A Boot task to do many wonderful things.

## Usage

FIXME: explanation

Run the `boot-nifi-pre` task:

    $ boot boot-nifi-pre

To use this in your project, add `[boot-nifi "0.1.0-SNAPSHOT"]` to your `:dependencies`
and then require the task:

    (require '[boot-nifi.core :refer [boot-nifi-pre]])

Other tasks include: `boot-nifi-simple`, `boot-nifi-post`, `boot-nifi-pass-thru`.

## License

Copyright © 2016 Big Solutions

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
