package org.jboss.seam.wiki.core.model;

import java.util.List;

public class WikiDocumentDefaults {

    WikiDocument template;

    public WikiDocumentDefaults() {}

    public WikiDocumentDefaults(WikiDocument template) {
        this.template = template;
    }

    public WikiDocument getTemplate() {
        return template;
    }

    public void setTemplate(WikiDocument template) {
        this.template = template;
    }

    public String getName() {
        return "New Document";
    }

    public String getContentText() {
        return "Edit this text...";
    }

    /**
     * @return a list of <tt>WikiTextMacro</tt> instances or null if <tt>getHeaderMacrosAsString()</tt> should be called.
     */
    public List<WikiTextMacro> getContentMacros() {
        return null;
    }

    public String[] getContentMacrosAsString() {
        return new String[0];
    }

    public String getHeaderText() {
        return null;
    }

    /**
     * @return a list of <tt>WikiTextMacro</tt> instances or null if <tt>getHeaderMacrosAsString()</tt> should be called.
     */
    public List<WikiTextMacro> getHeaderMacros() {
        return null;
    }

    public String[] getHeaderMacrosAsString() {
        return new String[0];
    }

    public String getFooterText() {
        return null;
    }

    /**
     * @return a list of <tt>WikiTextMacro</tt> instances or null if <tt>getHeaderMacrosAsString()</tt> should be called.
     */
    public List<WikiTextMacro> getFooterMacros() {
        return null;
    }

    public String[] getFooterMacrosAsString() {
        return new String[0];
    }

    public void setOptions(WikiDocument document) {}

}
