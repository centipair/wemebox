(ns centipair.routes.home
  (:require [centipair.core.contrib.template :as layout]
            [compojure.core :refer [defroutes GET]]
            [clojure.java.io :as io]
            [centipair.core.contrib.response :as response]))


(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))


(defn about-page []
  (layout/render "about.html"))


(defn test-page [request]
  (println request)
  (response/json-response {:test "success"}))


(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))
  (GET "/test" request (test-page request)))
