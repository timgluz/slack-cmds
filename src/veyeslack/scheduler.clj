(ns veyeslack.scheduler
  (:require [com.stuartsierra.component :as component]
            [clj-time.core :as dt]
            [immutant.scheduling :as qs] 
            [immutant.scheduling.joda]
            [veyeslack.jobs.notify :as notify-job]))

(defrecord JobScheduler [db]
  component/Lifecycle
  (start [this]
    (let [the-scheduler (qs/schedule
                          #(notify-job/execute! (:db this))
                           {:id :notifications
                            :cron "0 0 6 ? * MON-FRI"})]
      (println "#-- starting a JobScheduler")
      (assoc this :scheduler the-scheduler)))
  
  (stop [this]
    (println "#-- stopping scheduler")
    (qs/stop (qs/id :notifications))
    (dissoc this :scheduler)))

(defn create [db]
  (->JobScheduler db))
