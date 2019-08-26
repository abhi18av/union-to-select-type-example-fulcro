(ns app.ui.root
  (:require
    [app.model.session :as session]
    [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h3 button table td tr th tbody]]
    [com.fulcrologic.fulcro.dom.html-entities :as ent]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [com.fulcrologic.fulcro.components :as prim :refer [defsc]]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
    [com.fulcrologic.fulcro.ui-state-machines :as uism :refer [defstatemachine]]
    [com.fulcrologic.fulcro.components :as comp]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro.routing.legacy-ui-routers :as r :refer [defsc-router]]
    [com.fulcrologic.fulcro-css.css :as css]
    [com.fulcrologic.fulcro.algorithms.form-state :as fs]
    [clojure.string :as str]))

;; ;;;;;;;;;;;;;;;;
;; ;; Utils
;; ;;;;;;;;;;;;;;;;

;; (defn field [{:keys [label valid? error-message] :as props}]
;;   (let [input-props (-> props (assoc :name label) (dissoc :label :valid? :error-message))]
;;     (div :.ui.field
;;       (dom/label {:htmlFor label} label)
;;       (dom/input input-props)
;;       (dom/div :.ui.error.message {:classes [(when valid? "hidden")]}
;;         error-message))))

;; ;;;;;;;;;;;;;;;;
;; ;; SignUp
;; ;;;;;;;;;;;;;;;;

;; (defsc SignupSuccess [this props]
;;   {:query         ['*]
;;    :initial-state {}
;;    :ident         (fn [] [:component/id :signup-success])
;;    :route-segment ["signup-success"]
;;    :will-enter    (fn [app _] (dr/route-immediate [:component/id :signup-success]))}
;;   (div
;;     (dom/h3 "Signup Complete!")
;;     (dom/p "You can now log in!")))

;; (defsc Signup [this {:account/keys [email password password-again] :as props}]
;;   {:query             [:account/email :account/password :account/password-again fs/form-config-join]
;;    :initial-state     (fn [_]
;;                         (fs/add-form-config Signup
;;                           {:account/email          ""
;;                            :account/password       ""
;;                            :account/password-again ""}))
;;    :form-fields       #{:account/email :account/password :account/password-again}
;;    :ident             (fn [] session/signup-ident)
;;    :route-segment     ["signup"]
;;    :componentDidMount (fn [this]
;;                         (comp/transact! this [(session/clear-signup-form)]))
;;    :will-enter        (fn [app _] (dr/route-immediate [:component/id :signup]))}
;;   (let [submit!  (fn [evt]
;;                    (when (or (identical? true evt) (evt/enter-key? evt))
;;                      (comp/transact! this [(session/signup! {:email email :password password})])
;;                      (log/info "Sign up")))
;;         checked? (log/spy :info (fs/checked? props))]
;;     (div
;;       (dom/h3 "Signup")
;;       (div :.ui.form {:classes [(when checked? "error")]}
;;         (field {:label         "Email"
;;                 :value         (or email "")
;;                 :valid?        (session/valid-email? email)
;;                 :error-message "Must be an email address"
;;                 :autoComplete  "off"
;;                 :onKeyDown     submit!
;;                 :onChange      #(m/set-string! this :account/email :event %)})
;;         (field {:label         "Password"
;;                 :type          "password"
;;                 :value         (or password "")
;;                 :valid?        (session/valid-password? password)
;;                 :error-message "Password must be at least 8 characters."
;;                 :onKeyDown     submit!
;;                 :autoComplete  "off"
;;                 :onChange      #(m/set-string! this :account/password :event %)})
;;         (field {:label         "Repeat Password" :type "password" :value (or password-again "")
;;                 :autoComplete  "off"
;;                 :valid?        (= password password-again)
;;                 :error-message "Passwords do not match."
;;                 :onChange      #(m/set-string! this :account/password-again :event %)})
;;         (dom/button :.ui.primary.button {:onClick #(submit! true)}
;;           "Sign Up")))))

;; (declare Session)

;; ;;;;;;;;;;;;;;;;
;; ;; LogIn
;; ;;;;;;;;;;;;;;;;

;; (defsc Login [this {:account/keys [email]
;;                     :ui/keys      [error open?] :as props}]
;;   {:query         [:ui/open? :ui/error :account/email
;;                    {[:component/id :session] (comp/get-query Session)}
;;                    [::uism/asm-id ::session/session]]
;;    :css           [[:.floating-menu {:position "absolute !important"
;;                                      :z-index  1000
;;                                      :width    "300px"
;;                                      :right    "0px"
;;                                      :top      "50px"}]]
;;    :initial-state {:account/email "" :ui/error ""}
;;    :ident         (fn [] [:component/id :login])}
;;   (let [current-state (uism/get-active-state this ::session/session)
;;         {current-user :account/name} (get props [:component/id :session])
;;         initial?      (= :initial current-state)
;;         loading?      (= :state/checking-session current-state)
;;         logged-in?    (= :state/logged-in current-state)
;;         {:keys [floating-menu]} (css/get-classnames Login)
;;         password      (or (comp/get-state this :password) "")] ; c.l. state for security
;;     (dom/div
;;       (when-not initial?
;;         (dom/div :.right.menu
;;           (if logged-in?
;;             (dom/button :.item
;;               {:onClick #(uism/trigger! this ::session/session :event/logout)}
;;               (dom/span current-user) ent/nbsp "Log out")
;;             (dom/div :.item {:style   {:position "relative"}
;;                              :onClick #(uism/trigger! this ::session/session :event/toggle-modal)}
;;               "Login"
;;               (when open?
;;                 (dom/div :.four.wide.ui.raised.teal.segment {:onClick (fn [e]
;;                                                                         ;; Stop bubbling (would trigger the menu toggle)
;;                                                                         (evt/stop-propagation! e))
;;                                                              :classes [floating-menu]}
;;                   (dom/h3 :.ui.header "Login")
;;                   (div :.ui.form {:classes [(when (seq error) "error")]}
;;                     (field {:label    "Email"
;;                             :value    email
;;                             :onChange #(m/set-string! this :account/email :event %)})
;;                     (field {:label    "Password"
;;                             :type     "password"
;;                             :value    password
;;                             :onChange #(comp/set-state! this {:password (evt/target-value %)})})
;;                     (div :.ui.error.message error)
;;                     (div :.ui.field
;;                       (dom/button :.ui.button
;;                         {:onClick (fn [] (uism/trigger! this ::session/session :event/login {:username email
;;                                                                                              :password password}))
;;                          :classes [(when loading? "loading")]} "Login"))
;;                     (div :.ui.message
;;                       (dom/p "Don't have an account?")
;;                       (dom/a {:onClick (fn []
;;                                          (uism/trigger! this ::session/session :event/toggle-modal {})
;;                                          (dr/change-route this ["signup"]))}
;;                         "Please sign up!"))))))))))))

;; (def ui-login (comp/factory Login))

;; ;;;;;;;;;;;;;;;;
;; ;; Main
;; ;;;;;;;;;;;;;;;;


;; (defsc Main [this props]
;;   {:query         [:main/welcome-message]
;;    :initial-state {:main/welcome-message "Hi!"}
;;    :ident         (fn [] [:component/id :main])
;;    :route-segment ["main"]
;;    :will-enter    (fn [_ _] (dr/route-immediate [:component/id :main]))}
;;   (div :.ui.container.segment
;;     (h3 "Main")))




;; ;;;;;;;;;;;;;;;;
;; ;; Settings
;; ;;;;;;;;;;;;;;;;

;; (defsc Settings [this {:keys [:account/time-zone :account/real-name] :as props}]
;;   {:query         [:account/time-zone :account/real-name]
;;    :ident         (fn [] [:component/id :settings])
;;    :route-segment ["settings"]
;;    :will-enter    (fn [_ _] (dr/route-immediate [:component/id :settings]))
;;    :initial-state {}}
;;   (div :.ui.container.segment
;;     (h3 "Settings")))

;; (dr/defrouter TopRouter [this props]
;;   {:router-targets [Main Signup SignupSuccess Settings]})

;; (def ui-top-router (comp/factory TopRouter))

;; ;;;;;;;;;;;;;;;;
;; ;; Session
;; ;;;;;;;;;;;;;;;;

;; (defsc Session
;;   "Session representation. Used primarily for server queries. On-screen representation happens in Login component."
;;   [this {:keys [:session/valid? :account/name] :as props}]
;;   {:query         [:session/valid? :account/name]
;;    :ident         (fn [] [:component/id :session])
;;    :pre-merge     (fn [{:keys [data-tree]}]
;;                     (merge {:session/valid? false :account/name ""}
;;                       data-tree))
;;    :initial-state {:session/valid? false :account/name ""}})

;; (def ui-session (prim/factory Session))

;; ;;;;;;;;;;;;;;;;
;; ;; TopChrome
;; ;;;;;;;;;;;;;;;;

;; (defsc TopChrome [this {:root/keys [router current-session login]}]
;;   {:query         [{:root/router (comp/get-query TopRouter)}
;;                    {:root/current-session (comp/get-query Session)}
;;                    [::uism/asm-id ::TopRouter]
;;                    {:root/login (comp/get-query Login)}]
;;    :ident         (fn [] [:component/id :top-chrome])
;;    :initial-state {:root/router          {}
;;                    :root/login           {}
;;                    :root/current-session {}}}
;;   (let [current-tab (some-> (dr/current-route this this) first keyword)]
;;     (div :.ui.container
;;       (div :.ui.secondary.pointing.menu
;;         (dom/a :.item {:classes [(when (= :main current-tab) "active")]
;;                        :onClick (fn [] (dr/change-route this ["main"]))} "Main")
;;         (dom/a :.item {:classes [(when (= :settings current-tab) "active")]
;;                        :onClick (fn [] (dr/change-route this ["settings"]))} "Settings")
;;         (div :.right.menu
;;           (ui-login login)))
;;       (div :.ui.grid
;;         (div :.ui.row
;;           (ui-top-router router))))))

;; (def ui-top-chrome (comp/factory TopChrome))

;; ;;;;;;;;;;;;;;;;
;; ;; Bump Number
;; ;;;;;;;;;;;;;;;;

;; (defmutation bump-number [ignored]
;;   (action [{:keys [state]}]
;;           (swap! state update :root/number inc)))

;; (defsc BumpNumber [this {:root/keys [number]}]
;;        {:query         [:root/number]
;;         :initial-state {:root/number 0}}
;;        (dom/div
;;          (dom/h4 "This is an example.")
;;          (dom/button {:onClick #(comp/transact! this `[(bump-number {})])}
;;                      "You've clicked this button " number " times.")))

;; (def ui-bump-number (comp/factory BumpNumber))

;; ;;;;;;;;;;;;;;;;
;; ;; ROOT
;; ;;;;;;;;;;;;;;;;


;; (defsc Root [this {:root/keys [top-chrome]}]
;;   {:query             [{:root/top-chrome (comp/get-query TopChrome)}]
;;    :ident             (fn [] [:component/id :ROOT])
;;    :initial-state     {:root/top-chrome {}}}
;;   (div
;;     (ui-top-chrome top-chrome)
;;     (ui-bump-number {})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;;;;;;;;;;;;;;;;
;; VANILLA EXAMPLES
;;;;;;;;;;;;;;;;


;;;;;;;;;;;;;;;;
;; UNION TO SELECT TYPE EXAMPLES
;;;;;;;;;;;;;;;;


(defn person? [props] (contains? props :person/id))
(defn place? [props] (contains? props :place/id))
(defn thing? [props] (contains? props :thing/id))

(defn item-ident
      "Generate an ident from a person, place, or thing."
      [props]
      (cond
        (person? props) [:person/id (:person/id props)]
        (place? props) [:place/id (:place/id props)]
        (thing? props) [:thing/id (:thing/id props)]
        :else (log/error "Cannot generate a valid ident. Invalid props." props)))

(defn item-key
      "Generate a distinct react key for a person, place, or thing"
      [props] (str (item-ident props)))

(defn make-person [id n] {:person/id id :person/name n})
(defn make-place [id n] {:place/id id :place/name n})
(defn make-thing [id n] {:thing/id id :thing/label n})

(defsc PersonDetail [this {:person/keys [id name] :as props}]
       ; defsc-router expects there to be an initial state for each possible target. We'll cause this to be a "no selection"
       ; state so that the detail screen that starts out will show "Nothing selected". We initialize all three in case
       ; we later re-order them in the defsc-router.
       {:ident         (fn [] (item-ident props))
        :query         [:person/id :person/name]
        :initial-state {:person/id :no-selection}}
       (dom/div
         (if (= id :no-selection)
           "Nothing selected"
           (str "Details about person " name))))

(defsc PlaceDetail [this {:place/keys [id name] :as props}]
       {:ident         (fn [] (item-ident props))
        :query         [:place/id :place/name]
        :initial-state {:place/id :no-selection}}
       (dom/div
         (if (= id :no-selection)
           "Nothing selected"
           (str "Details about place " name))))

(defsc ThingDetail [this {:thing/keys [id label] :as props}]
       {:ident         (fn [] (item-ident props))
        :query         [:thing/id :thing/label]
        :initial-state {:thing/id :no-selection}}
       (dom/div
         (if (= id :no-selection)
           "Nothing selected"
           (str "Details about thing " label))))

(defsc PersonListItem [this
                       {:person/keys [id name] :as props}
                       {:keys [onSelect] :as computed}]
       {:ident (fn [] (item-ident props))
        :query [:person/id :person/name]}
       (dom/li {:onClick #(onSelect (item-ident props))}
               (dom/a {} (str "Person " id " " name))))

(def ui-person (comp/factory PersonListItem {:keyfn item-key}))

(defsc PlaceListItem [this {:place/keys [id name] :as props} {:keys [onSelect] :as computed}]
       {:ident (fn [] (item-ident props))
        :query [:place/id :place/name]}
       (dom/li {:onClick #(onSelect (item-ident props))}
               (dom/a {} (str "Place " id " : " name))))

(def ui-place (comp/factory PlaceListItem {:keyfn item-key}))

(defsc ThingListItem [this {:thing/keys [id label] :as props} {:keys [onSelect] :as computed}]
       {:ident (fn [] (item-ident props))
        :query [:thing/id :thing/label]}
       (dom/li {:onClick #(onSelect (item-ident props))}
               (dom/a {} (str "Thing " id " : " label))))

(def ui-thing (comp/factory ThingListItem item-key))

(defsc-router ItemDetail [this props]
              {:router-id      :detail-router
               :ident          (fn [] (item-ident props))
               :default-route  PersonDetail
               :router-targets {:person/id PersonDetail
                                :place/id  PlaceDetail
                                :thing/id  ThingDetail}}
              (dom/div "No route"))

(def ui-item-detail (comp/factory ItemDetail))

(defsc ItemUnion [this props]
       {:ident (fn [] (item-ident props))
        :query (fn [] {:person/id (comp/get-query PersonListItem)
                       :place/id  (comp/get-query PlaceListItem)
                       :thing/id  (comp/get-query ThingListItem)})}
       (cond
         (person? props) (ui-person props)
         (place? props) (ui-place props)
         (thing? props) (ui-thing props)
         :else (dom/div "Invalid ident used in app state.")))

(def ui-item-union (comp/factory ItemUnion {:keyfn item-key}))

(defsc ItemList [this {:keys [items]} {:keys [onSelect]}]
       {
        :initial-state (fn [p]
                           ; These would normally be loaded...but for demo purposes we just hand code a few
                           {:items [(make-person 1 "Tony")
                                    (make-thing 2 "Toaster")
                                    (make-place 3 "New York")
                                    (make-person 4 "Sally")
                                    (make-thing 5 "Pillow")
                                    (make-place 6 "Canada")]})
        :ident         (fn [] [:lists/id :singleton])
        :query         [{:items (comp/get-query ItemUnion)}]}
       (dom/ul :.ui.list
               (map (fn [i] (ui-item-union (comp/computed i {:onSelect onSelect}))) items)))

(def ui-item-list (comp/factory ItemList))

(defsc Root [this {:keys [item-list item-detail]}]
       {:query         [{:item-list (comp/get-query ItemList)}
                        {:item-detail (comp/get-query ItemDetail)}]
        :initial-state (fn [p] (merge
                                 (r/routing-tree
                                   (r/make-route :detail [(r/router-instruction :detail-router [:param/kind :param/id])]))
                                 {:item-list   (comp/get-initial-state ItemList nil)
                                  :item-detail (comp/get-initial-state ItemDetail nil)}))}
       (let [; This is the only thing to do: Route the to the detail screen with the given route params!
             showDetail (fn [[kind id]]
                            (comp/transact! this `[(r/route-to {:handler :detail :route-params {:kind ~kind :id ~id}})]))]
            ; devcards, embed in iframe so we can use bootstrap css easily
            (div {:key "example-frame-key"}
                 (dom/style ".boxed {border: 1px solid black}")
                 (table :.ui.table {}
                        (tbody
                          (tr
                            (th "Items")
                            (th "Detail"))
                          (tr
                            (td (ui-item-list (comp/computed item-list {:onSelect showDetail})))
                            (td (ui-item-detail item-detail))))))))



