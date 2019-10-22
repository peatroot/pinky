# pinky-api

An experimental expert system for querying a subset of Open Targets data, built using [clara-rules](http://www.clara-rules.org/).

## Usage

The `pinky-api` depends on data processed by `pinky-pipeline`, so ensure you have run that first. You should then create a symlink from `pinky-pipeline/data/processed` to `pinky-api/resources/records`.

You can now start the Clojure REPL with `lein repl`.

The base data, record/fact types, inference rules and queries can be loaded into a session object using the following statement, which will take a minute or so.

```
; see src/pinky_api/core.clj for details
(def s (load-session))
```

There are several example base queries (see `src/pinky_api/engine.clj`), such as `get-gene-by-symbol`. You can use them as follows:

```
(query s get-gene-by-symbol :?symbol "BRAF")
(query s get-drug-by-name :?name "BEPRIDIL")
(query s get-interactors-for-gene-by-ensg-id :?ensg-id "ENSG00000157764")
(query s get-direct-gwas-genes-for-disease-by-efo-id :?efo-id "EFO_0004527")
```

You should also be able to create new queries and rules in the REPL using the macros `defquery` and `defrule` (see [clara-rules](http://www.clara-rules.org/) for details). Note that triggering new rules requires the use of `(fire-rules s)`.

## Note

There are some files that are currently unused which set up a GraphQL server (using [Lavinia](https://github.com/walmartlabs/lacinia)). The aim is to eventually connect queries to such a server, but the available queries are completely unrelated at this time, coming from the Lavinia tutorial.

## License

Copyright © 2019 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
