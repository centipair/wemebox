(ns centipair.core.auth.user.models
  "This provides an interface to various types of databases
  The user-model methods in this namesapce has to be implemented by the database system file"
  (:require [centipair.core.auth.user.nosql :as user-model]
            [validateur.validation :refer :all]
            [centipair.core.utilities.validators :as v]))

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


(defn activate-account
  [registration-key]
  (user-model/activate-account registration-key))


(defn check-login [params]
  (user-model/check-login params))

;;validations
(defn email-exist-check
  [value]
  (if (v/has-value? value)
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
  (let [validation-result (registration-validator params)]
    (if (valid? validation-result)
      true
      [false {:validation-result {:errors validation-result}}])))


(def login-validator
  (validation-set
   (presence-of :username :message "Please enter the email address you have registered.")
   (presence-of :password :message "Please enter your password")))


(defn validate-user-login
  [params]
  (let [validation-result (login-validator params)]
    (if (valid? validation-result)
      true
      [false {:validation-result {:errors validation-result}}])))
