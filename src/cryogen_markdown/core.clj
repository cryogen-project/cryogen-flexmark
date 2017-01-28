(ns cryogen-markdown.core
  (:require [cryogen-core.markup :refer [markup-registry rewrite-hrefs]]
            [clojure.string :as s])
  (:import cryogen_core.markup.Markup
           com.vladsch.flexmark.parser.Parser
           com.vladsch.flexmark.html.HtmlRenderer
           com.vladsch.flexmark.Extension))

(defn rewrite-hrefs-transformer
  "A :replacement-transformer for use in markdown.core that will inject the
  given blog prefix in front of local links."
  [{:keys [blog-prefix]} text state]
  [(rewrite-hrefs blog-prefix text) state])

(defn markdown
  "Returns a Markdown (https://daringfireball.net/projects/markdown/)
  implementation of the Markup protocol."
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
             (.render renderer)))))))

(defn init []
  (swap! markup-registry conj (markdown)))
