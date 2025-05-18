(defproject order-history "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [com.github.seancorfield/next.jdbc "1.3.1002"]
                 [org.postgresql/postgresql "42.7.5"]
                 [ring/ring-core "1.14.0"]
                 [ring/ring-jetty-adapter "1.14.0"]
                 [metosin/reitit "0.8.0"]
                 [metosin/muuntaja "0.6.11"]
                 [camel-snake-kebab "0.4.3"]
                 [com.fooheads/rax "0.2.3"]
                 [com.github.seancorfield/honeysql "2.7.1310"]]

  :main ^:skip-aot order-history.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}}
  :plugins [[io.github.borkdude/lein-lein2deps "0.1.0"]])

