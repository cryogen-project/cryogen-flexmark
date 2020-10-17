(ns cryogen-flexmark.core
  (:require [cryogen-core.markup :refer [markup-registry rewrite-hrefs]]
            [clojure.string :as s])
  (:import cryogen_core.markup.Markup
           com.vladsch.flexmark.parser.Parser
           com.vladsch.flexmark.html.HtmlRenderer
           (com.vladsch.flexmark.util.data MutableDataSet)
           (com.vladsch.flexmark.ext.footnotes FootnoteExtension)
           (com.vladsch.flexmark.ext.gfm.strikethrough StrikethroughExtension)
           (com.vladsch.flexmark.ext.superscript SuperscriptExtension)
           (com.vladsch.flexmark.ext.tables TablesExtension)
           (java.util ArrayList)))

(defn markdown
  "Returns a Markdown (CommonMark) implementation of the Markup protocol."
  []
  (let [extensions [(FootnoteExtension/create)
                    (StrikethroughExtension/create)
                    (SuperscriptExtension/create)
                    (TablesExtension/create)]
        options (-> (MutableDataSet.)
                    (.set Parser/EXTENSIONS (ArrayList. extensions))
                    (.set HtmlRenderer/FENCED_CODE_LANGUAGE_CLASS_PREFIX "")
                    (.set HtmlRenderer/GENERATE_HEADER_ID true)
                    (.set HtmlRenderer/RENDER_HEADER_ID true))
        parser (.build (Parser/builder options))
        renderer (.build (HtmlRenderer/builder options))]
   (reify Markup
    (dir [this] "md")
    (exts [this] #{".md"})
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
