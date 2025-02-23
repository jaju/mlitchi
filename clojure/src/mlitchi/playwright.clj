(ns mlitchi.playwright
  (:import [com.microsoft.playwright Playwright BrowserType Browser Page]))

(defonce playwright (Playwright/create))
(defonce chromium (.chromium playwright))
(defonce safari (.webkit playwright))

(defn show-window [page]
  (.bringToFront page))

(defn create-window [start-url]
  (let [browser (.launch safari)
        page (.newPage browser)]
    (show-window page)
    (.navigate page start-url)
    browser))


(defn navigate [browser url]
  (let [page (.newPage browser)]
    (.navigate page url)))

(defn close-window [browser]
  (.close browser))

(comment
  (def b (create-window "https://www.google.com/"))
  (navigate b "https://www.google.com/")
  (close-window b)
  (let [browser (.launch safari)
        page (.newPage browser)]
    (.navigate page "https://www.google.com/")
    (.close browser)))
