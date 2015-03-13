(ns centipair.core.auth.user.routes
  (:require [compojure.core :refer :all]
            [centipair.layout :as layout]))


(defn register-page
  "Registration Page"
  []
  (layout/render "core/user/register.html"))

(defn login-page
  "Login Page"
  []
  (layout/render "core/user/login.html"))



(defroutes user-routes 
  (GET "/register" [] (register-page))
  (GET "/login" [] (login-page)))
