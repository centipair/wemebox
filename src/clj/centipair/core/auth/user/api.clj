(ns centipair.core.auth.user.api
   (:use compojure.core)
   (:require [liberator.core :refer [resource defresource]]
             [centipair.core.contrib.response :as response]
             [centipair.core.auth.user.models :as user-models]))



(defresource api-user-register []
  :available-media-types ["application/json"]
  :allowed-methods [:post]
  :processable? (fn [context]
                  (user-models/validate-user-registration (:params (:request context))))
  :handle-unprocessable-entity (fn [context]
                                 (:validation-result context))
  :post! (fn [context] (user-models/register-user (:params (:request context))))
  :handle-created (fn [context] (response/liberator-json-response {:register "success"})))


(defresource api-user-login []
  :available-media-types ["application/json"]
  :allowed-methods [:post]
  :processable? (fn [context]
                  (user-models/check-login (:params (:request context))))
  :handle-unprocessable-entity (fn [context]
                                 (:validation-result context))
  :post! (fn [context]
           {:auth-token (user-models/login (:params (:request context)))})
  :handle-created (fn [context]
                    (response/liberator-json-response-cookies {:message "success"} {:auth-token {:value (:auth-token context)}})))

(defroutes api-user-routes
  (POST "/api/register" [] (api-user-register))
  (POST "/api/login" [] (api-user-login)))
