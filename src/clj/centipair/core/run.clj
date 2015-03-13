(ns centipair.core.run
  (:use centipair.handler)
  (:require [immutant.web :as web])
  (:gen-class))

(defn start-immutant-server [args]
  (web/run
    app
    {}))

(defn -main [& {:as args}]
  (println "running immutant too#########################################################################################################3")
  (start-immutant-server args))
