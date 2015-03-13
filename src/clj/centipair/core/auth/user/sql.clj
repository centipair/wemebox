(ns centipair.core.auth.user.sql
  (:use korma.core
        korma.db
        centipair.core.db.connection
        centipair.core.contrib.mail
        centipair.core.contrib.time
        centipair.core.contrib.cookies)
  (:require [centipair.core.contrib.cryptography :as crypt]))



(defentity user_session)

(defentity user_account
  (pk :user_account_id)
  (has-many user_session))

(defentity user_profile)

(defentity user_registration)

(defentity user_password_reset)

(def login-error {:status-code 422 :errors {:username ["Email or Password is incorrect"]}})

(def user-inactive-error {:status-code 422 :errors {:username ["This account is inactive"]}})


(defn login-success
  "What happens after a successful login"
  [user-account]
  (if (:is_admin user-account)
    {:status-code 200 :message "admin" :redirect "/admin"}
    {:status-code 200 :message "user" :redirect "/user/dashboard"}))

(def registration-success {:status-code 200 :message "Registration success"})

(defn select-user-email
  "Select user account based on email"
  [email]
  (first (select user_account (where {:email email}))))


(defn new-user-account
  "Creates new user account"
  [params]
  (insert user_account (values {:username (:email params)
                                :password (crypt/encrypt-password (:password params))
                                :email (:email params)
                                :created_on (sql-time-now)})))


(defn create-registration-request
  "Creates registration request"
  [user-account]
  (insert user_registration (values {:user_account_id (:user_account_id user-account)
                                     :registration_key (crypt/random-base64 8)})))


(defn register-user
  "User registration db operations"
  [params]
  (let [user-account (new-user-account params)
        registration-request (create-registration-request user-account)]
      (future (send-registration-email (assoc registration-request :email (:email user-account))))
      registration-success))


(defn create-user-session
  "Creating user session for user account"
  [user-account]
  (let [auth-token (crypt/random-base64 48)]
    (do
      (insert user_session (values {:user_account_id (:user_account_id user-account)
                                    :auth_token auth-token 
                                    :session_expiry (set-time-expiry 23)}))
      (set-cookies :auth-token auth-token)
      (login-success user-account))))


(defn login
  "Login DB operations"
  [params]
  (let [user-account (select-user-email (:username params))]
    (if (nil? user-account)
      login-error
      (if (not (:active user-account))
        user-inactive-error
        (if (crypt/check-password (:password params) (:password user-account))
          (create-user-session user-account)
          login-error)))))


(defn get-user-session
  "Gets user session based on auth-token"
  [auth-token]
  (first (select user_session (where {:auth_token auth-token}))))


(defn delete-session [auth-token]
  (do
    (delete user_session (where {:auth_token auth-token}))
    (delete-cookies :auth-token))
  {:status-code 301})


(defn logout-user
  []
  (let [auth-token (get-cookies :auth-token)]
    (if (nil? auth-token)
      {:status-code 301}
      (delete-session auth-token))))


(defn activate-account
  "Activates user account based on registration key"
  [registration-key]
  (let [user-registration (first (select user_registration (where {:registration_key registration-key})))
        user-id (:user_account_id user-registration)]
    (if (nil? user-id)
      false
      (do 
        (transaction
         (update user_account (set-fields {:active true}) (where {:user_account_id user-id}))
         (update user_registration (set-fields {:activated true})))
        true))))


(defn password-reset-email
  "Password reset save and send email"
  [params]
  (let [email (:email params)
        user-account (select-user-email email)]
    (if (nil? user-account)
      {:status-code 422 :errors {:email ["This email is not registered"]}}
      (do
        (let [password-reset-key (crypt/random-base64 8)
              password-reset (insert user_password_reset 
                                     (values 
                                      {:user_account_id (:user_account_id user-account)
                                       :password_reset_key password-reset-key
                                       :key_expiry (set-time-expiry 48)}))]
          (future (send-password-reset-email email password-reset-key)))
        
        {:status-code 200}))))


(defn get-password-reset
  "get password reset data"
  [password-reset-key]
  (first (select user_password_reset (where {:password_reset_key password-reset-key}))))


(defn valid-password-reset-key?
  "Check whether valid password reset key"
  [password-reset-key]
  (let [password-reset (get-password-reset password-reset-key)]
    (if (nil? password-reset)
      false
      (if (time-expired? (:key_expiry password-reset))
        false
        true))))


(defn commit-reset-password
  "Save password reset"
  [params]
  (let [password-reset (get-password-reset (:password-reset-key params))]
    (if (nil? password-reset)
      {:status-code 422 :message "Invalid key"}
      (do
        (update user_account 
                (set-fields 
                 {:password (crypt/encrypt-password (:password params))}) 
                (where {:user_account_id (:user_account_id password-reset)}))
        {:status-code 200}))))


(defn get-user-session-account
  "gets user account from session"
  [auth-token]    
  (select user_account
          (fields :username :is_admin :user_session.session_expiry)
          (with user_session)
          (join :inner user_session (= :user_session.user_account_id :user_account_id))
          (where {:user_session.auth_token auth-token})))


(defn valid-session? [user-session-account]
  (if (time-expired? (:session_expiry user-session-account))
    false
    true))


(defn get-user-session
  "Gets the session data for current logged in user"
  []
  (let [auth-token (get-cookies :auth-token)]
    (if (nil? auth-token)
      nil
      (let [user-session-account (first (get-user-session-account auth-token))]
        (if (valid-session? user-session-account)
          user-session-account
          nil)))))
