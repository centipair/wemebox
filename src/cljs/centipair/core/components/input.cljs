(ns centipair.core.components.input
  (:require [reagent.core :as reagent :refer [atom]]
            [centipair.core.style :as style]
            [centipair.core.utilities.validators :refer [has-value?]]
            [centipair.core.components.editor :refer [markdown-editor]]))


(defn update-value
  [field value]  
  (reset! field (assoc @field :value value)))

(defn update-image-spec
  [key field value]
  (let [dim (key @field)
        new-dim (assoc dim :value value)]
    
    (reset! field (assoc @field key new-dim))))


(defn update-image-spec-check
  [field checked?]
  (let [crop (:crop @field)]
    (if checked?
      (reset! field (assoc @field :crop (assoc crop :checked "checked")))
      (reset! field (assoc @field :crop (assoc crop :checked ""))))))


(defn update-check
  [field checked?]
  (if checked?
    (reset! field (assoc @field :checked "checked"))
    (reset! field (assoc @field :checked ""))))

(defn reset-image-spec
  [field]
  (do 
    (update-image-spec :width field "")
    (update-image-spec :height field "")
    (update-image-spec-check field false)))


(defn update-radio
  [field value]
  (swap! field assoc :value value))


(defn text
  [field]
  [:div {:class (if (nil? (:class-name @field)) style/bootstrap-input-container-class (:class-name @field)) :key (str "container-" (:id @field))}
   [:label {:for (:id @field) :class "col-sm-2 control-label" :key (str "label-" (:id @field))} (:label @field)]
   [:div {:class (if (nil? (:size @field)) "col-sm-6" (str "col-sm-" (:size @field))) :key (str "divider-" (:id @field))}
    [:input {:class "form-control"
             :type (:type @field)
             :id (:id @field)
             :key (:id @field)
             :placeholder
             (if (nil? (:placeholder @field))
               ""
               (:placeholder @field))
             :value (:value @field)
             :on-change #(update-value field (-> % .-target .-value) )
             }]]
   [:label {:class "col-sm-4 message-label" :key (str "message-" (:id @field))} (if (nil? (:message @field))
             ""
             (:message @field))]])

(defn textarea
  [field]
  [:div {:class (if (nil? (:class-name @field)) style/bootstrap-input-container-class (:class-name @field)) :key (str "container-" (:id @field))}
   [:label {:for (:id @field) :class "col-sm-2 control-label" :key (str "label-" (:id @field))} (:label @field) ]
   [:div {:class "col-sm-6" :key (str "divider-" (:id @field))}
    [:textarea {:class "form-control"
                :type (:type @field) :id (:id @field)
                :placeholder
                (if (nil? (:placeholder @field))
                  ""
                  (:placeholder @field))
                :rows "5"
                :value (:value @field)
                :on-change #(update-value field (-> % .-target .-value) )
                :key (:id @field)
             }]]
   [:label {:class "col-sm-4 message-label" :key (str "message-" (:id @field))} (if (nil? (:message @field))
             ""
             (:message @field))]])


(defn hidden
  [field]
  [:input {:type "hidden" :value (:value @field) :id (:id @field) :key (:id @field)}])


(defn make-valid-image-spec [field]
  (do 
    (swap! field assoc :message "" :class-name style/bootstrap-input-container-class)
    (let [width-dim (:width @field)
          height-dim (:height @field)]
      (swap! field assoc :width (assoc width-dim :message ""))
      (swap! field assoc :height (assoc height-dim :message ""))))
  true)

(defn make-valid
  [field]
  (if (= (:type @field) "image-spec")
    (make-valid-image-spec field)
    (swap! field assoc :message "" :class-name style/bootstrap-input-container-class))
  true)


(defn valid-field?
  [field]
  (if (nil? (:validator @field))
    (do 
      (make-valid field)
      true)
    (let [result ((:validator @field) @field)]
      (if (:valid result)
        (make-valid field)
        (do
          (swap! field assoc
                 :message (:message result)
                 :class-name style/bootstrap-input-container-class-error)
          false)))))


(defn image-spec-error [errors field key key-id]
  (if (not (nil? (key-id errors)))
    (let [dim (key @field)]
      
      (swap! field assoc key
             (assoc dim 
               :message (first (key-id errors))))
      (swap! field assoc :class-name style/bootstrap-input-container-class-error))
    (swap! field assoc :class-name style/bootstrap-input-container-class)))

(defn append-image-spec-error [errors field]
  (let [width-key (keyword (:id (:width @field)))
        height-key (keyword (:id (:height @field)))
        crop-key (keyword (:id (:crop @field)))]
    (do
      (image-spec-error errors field :width width-key)
      (image-spec-error errors field :height height-key))))

(defn append-error
  [errors field]
  (if (= "image-spec" (:type @field))
    (append-image-spec-error errors field)
    (let [key (keyword (:id @field))]
      (if (not (nil? (key errors)))
        (swap! field assoc
               :message (first (key errors))
               :class-name style/bootstrap-input-container-class-error)))))


(defn valid-form?
  [form-fields]
  (apply = true (doall (map valid-field? form-fields))))


(defn perform-action
  [form action form-fields]
  (if (valid-form? form-fields)
    (do
      (swap! form assoc :message "")
      (action))
    (swap! form assoc :message "Form error!")))


(defn button
  [form form-fields action-button]
  [:div {:class style/bootstrap-input-container-class}
   [:div {:class "col-sm-offset-2 col-sm-6"}
    [:a {:class style/bootstrap-primary-button-class
         :on-click #(perform-action form (:on-click @action-button) form-fields)
         :disabled ""
         :key (:id @action-button)
         } 
     (:label @action-button)]]])


(defn plain-button
  [form form-fields action-button]
  [:div {:class style/bootstrap-input-container-class}
   
    [:a {:class style/bootstrap-primary-button-class
         :on-click #(perform-action form (:on-click @action-button) form-fields)
         :disabled ""
         :key (:id @action-button)
         } 
     (:label @action-button)]])



(defn checkbox
  [field]
  [:div {:class (if (nil? (:class-name @field)) style/bootstrap-input-container-class (:class-name @field)) :key (str "container-" (:id @field))}
   [:label {:for (:id @field) :class "col-sm-2 control-label" :key (str "label-" (:id @field))} (:label @field) ]
   [:div {:class "col-sm-6" :key (str "divider-" (:id @field))}
    [:div {:class "checkbox" :key (str "checkbox-" (:id @field))}
     [:label {:key (str "container-label-" (:id @field))}
      [:input {:type (:type @field) :id (:id @field)
               :value (:value @field)
               :on-change #(update-check field (-> % .-target .-checked) )
               :checked (:checked @field)
               :key (str "key-" (:id @field))
               }] (str " "(:description @field))]]]
   [:label {:class "col-sm-4 message-label"} (if (nil? (:message @field))
             ""
             (:message @field))]])


(defn plain-checkbox
  [field]
  [:div {:class "checkbox"}
   [:label [:input {:type "checkbox"
                    :value (:value @field)
                    :on-change #(update-check field (-> % .-target .-checked) )
                    :checked (:checked @field)
                    :key (str "key-" (:id @field))}]
    (str " " (:label @field))]])


(defn select-option [select-value option]
    [:option {:key (:value option)
              :value (:value option)
              } (:label option)])


(defn select [field]
  [:div {:class (if (nil? (:class-name @field)) style/bootstrap-input-container-class (:class-name @field))}
   [:label {:for (:id @field) :class "col-sm-2 control-label"} (:label @field)]
   [:div {:class "col-sm-6"}
    [:select {:key (:id @field)
              :class "form-control"
              :type (:type @field) :id (:id @field)
              :on-change #(update-value field (-> % .-target .-value) )
              :value (:value @field)
             }
     (doall (map (partial select-option (:value @field)) (:options @field)))
     ]]
   [:label {:class "col-sm-4 message-label"} (if (nil? (:message @field))
             ""
             (:message @field))]])


(defn radio-option [field option]
  [:div {:class "radio" :key (str "radio-container-" (:id option))} 
       [:label {:key (str "radio-label-" (:id option))}
        [:input {:type "radio"
                 :name (:name option) 
                 :id (:id option)
                 :value (:value option)
                 :on-change #(update-radio field (-> % .-target .-value))
                 :checked (if (= (:value option) (:value @field)) "checked" "")
                 :key (:id option)
                 }]
        (str " " (:label option))]])

(defn radio
  [field]
  [:div {:class (if (nil? (:class-name @field)) style/bootstrap-input-container-class (:class-name @field)) :key (str "container-" (:id @field))}
   [:label {:for (:id @field) :class "col-sm-2 control-label" :key (str "label-" (:id @field))} (:label @field) ]
   [:div {:class "col-sm-6" :key (str "divider-" (:id @field))}
    (doall (map (partial radio-option field) (:options @field)))]])


(defn subheading
  [field]
  [:div {:class style/bootstrap-input-container-class 
         :id (:id @field)
         :key (str "subheading-" (:id @field))} 
   [:h4 (:label @field)] (if (not (nil? (:description @field)) ) [:span (:description @field)])])


(defn image-spec
  [field]
  [:div {:class (if (nil? (:class-name @field)) style/bootstrap-input-container-class (:class-name @field))}
   [:label {:for (:id @field) :class "col-sm-2 control-label"} (:label @field)]
   [:div {:class "col-sm-1"}
    [:label {:class "control-label"} "Width"]
    [:input {:class "form-control"
             :type (:type "text") :id (:id (:width @field))
             :placeholder
             (if (nil? (:placeholder (:width @field)))
               ""
               (:placeholder (:width @field)))
             :value (:value (:width @field))
             :on-change #(update-image-spec :width field (-> % .-target .-value))}] 
    [:label {:class "control-label"} (:message (:width @field))]]
   
   [:div {:class "col-sm-1"}
    [:label {:class "control-label"} "Height"]
    [:input {:class "form-control"
             :type (:type "text") :id (:id (:height @field))
             :placeholder
             (if (nil? (:placeholder (:height @field)))
               ""
               (:placeholder (:height @field)))
             :value (:value (:height @field))
             :on-change #(update-image-spec :height field (-> % .-target .-value))}]
    [:label {:class "control-label"} (:message (:height @field))]]
   [:div {:class "col-sm-2"}
    [:div {:class "checkbox"}
     [:label
      [:input {:type "checkbox" :id (:id (:crop @field))
               :value (:value (:crop @field))
               :on-change #(update-image-spec-check field (-> % .-target .-checked) )
               :checked (:checked (:crop @field))
               }] (str " " (:description (:crop @field)))]]]
   [:label {:class "col-sm-4 message-label"} (if (nil? (:message @field))
             ""
             (:message @field))]])


(defn input-field [field]
  (case (:type @field)
    "text" (text field)
    "email" (text field)
    "textarea" (textarea field)
    "password" (text field)
    "checkbox" (checkbox field)
    "select" (select field)
    "radio" (radio field)
    "hidden" (hidden field)
    "subheading" (subheading field)
    "image-spec" (image-spec field)))


(defn form-aligned [form form-fields action-button]
  [:form {:class "form-horizontal"}
   [:legend [:h3 (:title @form)] [:span {:class "form-error"} (:message @form)]]
   (doall (map input-field form-fields))
   [:div {:class "pure-controls"} (button form form-fields action-button)]])




(defn plain-text
  [field]
  [:div {:class (if (nil? (:class-name @field)) style/bootstrap-input-container-class (:class-name @field)) :key (str "container-" (:id @field))}
   [:label {:for (:id @field) :class "control-label" :key (str "label-" (:id @field))} (:label @field)]
    [:input {:class "form-control"
             :type (:type @field)
             :id (:id @field)
             :key (:id @field)
             :placeholder
             (if (nil? (:placeholder @field))
               ""
               (:placeholder @field))
             :value (:value @field)
             :on-change #(update-value field (-> % .-target .-value) )
             }]
   [:label {:class "message-label" :key (str "message-" (:id @field))} (if (nil? (:message @field))
             ""
             (:message @field))]])



(defn plain-textarea
  [field]
  [:div {:class (if (nil? (:class-name @field)) style/bootstrap-input-container-class (:class-name @field)) :key (str "container-" (:id @field))}
   [:label {:for (:id @field) :class "control-label" :key (str "label-" (:id @field))} (:label @field) ]
   
    [:textarea {:class "form-control"
                :type (:type @field) :id (:id @field)
                :placeholder
                (if (nil? (:placeholder @field))
                  ""
                  (:placeholder @field))
                :rows "5"
                :value (:value @field)
                :on-change #(update-value field (-> % .-target .-value) )
                :key (:id @field)
             }]
   [:label {:class "message-label" :key (str "message-" (:id @field))} (if (nil? (:message @field))
             ""
             (:message @field))]])




(defn plain-input-field [field]
  (case (:type @field)
    "text" (plain-text field)
    "email" (plain-text field)
    "textarea" (plain-textarea field)
    "password" (plain-text field)
    "subheading" (subheading field)
    "markdown" (markdown-editor field)
    "checkbox" (plain-checkbox field)
    ))


(defn form-plain [form form-fields action-button]
  [:form
   [:legend [:h3 (:title @form)] [:span {:class "form-error"} (:message @form)]]
   (doall (map plain-input-field form-fields))
   (plain-button form form-fields action-button)
   ]
  )

(defn reset-text-field
  [field]
  (if (nil? (:default @field))
    (update-value field "")
    (update-value field (:default @field))))

(defn reset-input [field]
  (case (:type @field)
    "text" (reset-text-field field)
    "email" (update-value field "")
    "textarea" (update-value field "")
    "password" (update-value field "")
    "checkbox" (update-check field false)
    "select" (update-value field "")
    "radio" (update-radio field "")
    "hidden" (update-value field nil)
    "image-spec" (reset-image-spec field)
    "markdown" (update-value field "")))


(defn reset-inputs [fields]
  (doseq [field fields]
    (reset-input field)))

(defn clear-error [field]
  (if (= (:type field) "form")
    (swap! field assoc
           :message "")
  (swap! field assoc :message ""
         :class-name style/bootstrap-input-container-class)))

(defn clear-form-errors [fields]
  (doseq [field fields]
    (clear-error field)))
