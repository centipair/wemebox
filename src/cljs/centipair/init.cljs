(ns centipair.init
  (:require [centipair.core.components.notifier :as notifier]))


(defn ^:export init-app []
  (do 
    (notifier/render-notifier-component)))
