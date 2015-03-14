(defproject centipair "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :source-paths ["src/clj" "src/cljs"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [ring-server "0.4.0"]
                 [selmer "0.8.0"]
                 [com.taoensso/timbre "3.4.0"]
                 [com.taoensso/tower "3.0.2"]
                 [markdown-clj "0.9.63"]
                 [environ "1.0.0"]
                 [im.chit/cronj "1.4.3"]
                 [compojure "1.3.2"]
                 [ring/ring-defaults "0.1.3"]
                 [ring/ring-session-timeout "0.1.0"]
                 [ring-middleware-format "0.4.0"]
                 [noir-exception "0.2.3"]
                 [crypto-random "1.2.0"]
                 [danlentz/clj-uuid "0.1.3-SNAPSHOT"]
                 [bouncer "0.3.2"]
                 [prone "0.8.1"]
                 [org.immutant/immutant "2.0.0-beta1"]
                 [com.draines/postal "1.11.3"]
                 [liberator "0.12.2"]
                 [com.novemberain/validateur "2.4.2"]
                 [cheshire "5.4.0"]
                 [korma "0.4.0"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [org.postgresql/postgresql "9.2-1002-jdbc4"]
                 [clj-time "0.9.0"]
                 [buddy "0.4.1"]
                 [clojurewerkz/cassaforte "2.0.0"]
                 [org.clojure/clojurescript "0.0-2913"]
                 [reagent "0.5.0-alpha3"]
                 [secretary "1.2.1"]
                 [cljs-ajax "0.3.10"]
                 [enfocus "2.1.1"]
                 [com.cognitect/transit-cljs "0.8.205"]]
  :main centipair.core.run
  :min-lein-version "2.5.0"
  :uberjar-name "wemebox.jar"
  :repl-options {:init-ns centipair.handler}
  :jvm-opts ["-server"]
  
  :plugins [[lein-ring "0.9.1"]
            [lein-environ "1.0.0"]
            [lein-ancient "0.6.0"]
            [lein-cljsbuild "1.0.5"]
            ]
  

  :ring {:handler centipair.handler/app
         :init    centipair.handler/init
         :destroy centipair.handler/destroy
         :uberwar-name "centipair.war"}
  

  
  :profiles
  {:uberjar {:omit-source true
             :env {:production true}
             :aot :all}
   :production {:ring {:open-browser? false
                       :stacktraces?  false
                       :auto-reload?  false}}
   :dev {:dependencies [[ring-mock "0.1.5"]
                        [ring/ring-devel "1.3.2"]
                        [pjstadig/humane-test-output "0.7.0"]
                        ]
         
         :injections [(require 'pjstadig.humane-test-output)
                      (pjstadig.humane-test-output/activate!)]
         :env {:dev true}}}
  :clean-targets ^{:protect false} ["resources/public/js/main.js"
                                    "resources/public/js/admin-main.js"]
  :cljsbuild {:builds
               [{;; CLJS source code path
                 :id "release"
                 :source-paths ["src/cljs"]
                 ;; Google Closure (CLS) options configuration
                 :compiler {;; CLS generated JS script filename
                            :output-to "resources/public/js/main.js"
                            
                            ;; minimal JS optimization directive
                            ;;:optimizations :whitespace
                            :optimizations :advanced
                            ;; generated JS code prettyfication
                            ;;:pretty-print true
                            :pretty-print false
                            :preamble ["reagent/react.min.js"]
                            }}
                {;; CLJS source code path
                 :id "dev"
                 :source-paths ["src/cljs"]
                 ;; Google Closure (CLS) options configuration
                 :compiler {;; CLS generated JS script filename
                            :output-to "resources/public/js/main.js"
                            
                            ;; minimal JS optimization directive
                            :optimizations :whitespace
                            ;; generated JS code prettyfication
                            :pretty-print true
                            :preamble ["reagent/react.min.js"]
                            }}
                
                ]})
