(ns cryogen-flexmark.core
  (:require [cryogen-core.markup :refer [markup-registry rewrite-hrefs]]
            [clojure.string :as s])
  (:import cryogen_core.markup.Markup
           com.vladsch.flexmark.parser.Parser
           com.vladsch.flexmark.html.HtmlRenderer
           com.vladsch.flexmark.Extension))

(defn markdown
  "Returns a Markdown (CommonMark) implementation of the Markup protocol."
  []
  (let [parser (.build (Parser/builder))
        renderer (.build (HtmlRenderer/builder))]
   (reify Markup
    (dir [this] "md")
    (ext [this] ".md")
    (render-fn [this]
      (fn [rdr config]
        (->> (java.io.BufferedReader. rdr)
             (line-seq)
             (s/join "\n")
             (.parse parser)
             (.render renderer)
             (rewrite-hrefs (:blog-prefix config))))))))

(defn init []
  (swap! markup-registry conj (markdown)))
