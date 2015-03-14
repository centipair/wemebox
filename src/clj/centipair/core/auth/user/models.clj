(ns centipair.core.auth.user.models
  "This provides an interface to various types of databases
  The user-model methods in this namesapce has to be implemented by the database system file"
  (:require [centipair.core.auth.user.sql :as user-model]
            [validateur.validation :refer :all]
            [centipair.core.utilities.validation :as nv]))

;;Interface
(defn register-user
  [params]
  (user-model/register-user params))


(defn login
  [params]
  (user-model/login params))


(defn password-reset-email
  [params]
  (user-model/password-reset-email params))


(defn select-user-email
  [value]
  (user-model/select-user-email value))




;;validations
(defn email-exist-check
  [value]
  (if (nv/has-value? value)
    (if (nil? (select-user-email value))
      true
      false)))


(def registration-validator
  (validation-set
   (presence-of :email :message "Your email address is required for registration")
   (presence-of :password :message "Please choose a password")
   (validate-by :email email-exist-check :message "This email already exists")))


(defn validate-user-registration
  [params]
  (println params)
  (let [validation-result (registration-validator params)]
    (if (valid? validation-result)
      true
      [false {:validation-result {:errors validation-result}}])))
