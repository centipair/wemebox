(ns centipair.core.errors)


;;validation result format {:email #{"not a valid email"}}

(defn validation-error [key message]
  [false {:validation-result {:errors {key #{message}}}}])
