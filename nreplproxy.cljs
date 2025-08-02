#!/usr/bin/env -S npx nbb
(ns nreplproxy
  (:require
    ["net" :as net]
    ["fs" :as fs]
    ["bencode$default" :as bencode]
    [clojure.string :refer [includes?]]
    [applied-science.js-interop :as j]))

(defn- decode-and-log [buffer direction]
  (loop []
    (when (> (.-length @buffer) 0)
      (let [msg (try
                  (bencode/decode @buffer "utf8")
                  (catch js/Error e
                    (when-not (or (includes? (.-message e) "Unexpected end of buffer")
                                  (includes? (.-message e) "Not a string")
                                  (includes? (.-message e) "Invalid data"))
                      (js/console.error "Bencode stream error:" e))
                    :decode-error))]
        (when (not= msg :decode-error)
          (let [bytes-consumed (bencode/encodingLength msg)]
            (js/console.log (str "\n--- " direction " ---"))
            (js/console.dir msg {:depth nil})
            (swap! buffer #(.slice % bytes-consumed))
            (recur)))))))

(defn- main []
  (let [nrepl-port-file ".nrepl-port"]
    (when-not (fs/existsSync nrepl-port-file)
      (js/console.error "Error: .nrepl-port file not found.")
      (j/assoc! js/process :exitCode 1)
      (j/call js/process :exit))

    (let [original-port-content (fs/readFileSync nrepl-port-file "utf8")
          target-port (js/parseInt original-port-content)]

      (.on js/process "exit"
           (fn []
             (let [current-port-content (fs/readFileSync nrepl-port-file "utf8")]
               (js/console.log (str "Updating .nrepl-port from " current-port-content " to " original-port-content))
               (fs/writeFileSync nrepl-port-file original-port-content))))
      (.on js/process "SIGINT" #(.exit js/process))

      (let [proxy-server
            (net/createServer
              (fn [client-socket]
                (js/console.log "Client connected to proxy.")
                (let [server-socket (net/createConnection #js {:port target-port})
                      client-buffer (atom (js/Buffer.alloc 0))
                      server-buffer (atom (js/Buffer.alloc 0))]

                  (.on client-socket "data"
                       (fn [data]
                         (swap! client-buffer #(js/Buffer.concat (clj->js [% data])))
                         (decode-and-log client-buffer "Client -> Server")
                         (.write server-socket data)))

                  (.on server-socket "data"
                       (fn [data]
                         (swap! server-buffer #(js/Buffer.concat (clj->js [% data])))
                         (decode-and-log server-buffer "Server -> Client")
                         (.write client-socket data)))

                  (.on client-socket "close"
                       (fn []
                         (js/console.log "Client disconnected.")
                         (.end server-socket)))

                  (.on server-socket "close"
                       (fn []
                         (js/console.log "Server disconnected.")
                         (.end client-socket)))

                  (.on client-socket "error"
                       (fn [err]
                         (js/console.error "Client socket error:" err)
                         (.end server-socket)))

                  (.on server-socket "error"
                       (fn [err]
                         (js/console.error "Server socket error:" err)
                         (.end client-socket))))))]

        (.listen proxy-server 0 "127.0.0.1"
                 (fn []
                   (let [proxy-port (-> proxy-server .address .-port)]
                     (js/console.log (str "nREPL proxy started on port " proxy-port))
                     (js/console.log (str "Proxying to nREPL server on port " target-port))
                     (js/console.log (str "Updating .nrepl-port from " target-port " to " proxy-port))
                     (fs/writeFileSync nrepl-port-file (str proxy-port)))))))))

(main)
