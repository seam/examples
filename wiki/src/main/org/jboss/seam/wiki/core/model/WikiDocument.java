package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.Length;
import org.jboss.seam.wiki.core.search.annotations.Searchable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "WIKI_DOCUMENT")
@org.hibernate.annotations.ForeignKey(name = "FK_WIKI_DOCUMENT_NODE_ID")
//TODO: @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)

@org.hibernate.search.annotations.Indexed
@Searchable(description = "Documents")

public class WikiDocument extends WikiFile<WikiDocument> implements Serializable {

    public static final String MACRO_DISABLE_CONTENT_MARKUP = "disableContentMarkup";

    @Column(name = "NAME_AS_TITLE", nullable = false)
    private boolean nameAsTitle = true;

    @Column(name = "ENABLE_COMMENTS", nullable = false)
    private boolean enableComments = false;

    @Column(name = "ENABLE_COMMENT_FORM", nullable = false)
    private boolean enableCommentForm = true;

    @Column(name = "ENABLE_COMMENTS_ON_FEEDS", nullable = false)
    private boolean enableCommentsOnFeeds = true;

    @Column(name = "HEADER", nullable = true)
    @Length(min = 0, max = 1023)
    private String header;
    @Column(name = "HEADER_MACROS", nullable = true, length = 1023)
    private String headerMacrosString;

    @Column(name = "CONTENT", nullable = false)
    @Length(min = 0, max = 32767)
    @Basic(fetch = FetchType.LAZY) // Lazy loaded through bytecode instrumentation
    @org.hibernate.search.annotations.Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    @Searchable(description = "Content")
    private String content;
    @Column(name = "CONTENT_MACROS", nullable = true, length = 1023)
    private String contentMacrosString;

    @Column(name = "FOOTER", nullable = true)
    @Length(min = 0, max = 1023)
    private String footer;
    @Column(name = "FOOTER_MACROS", nullable = true, length = 1023)
    private String footerMacrosString;

    public WikiDocument() {
        super();
        WikiDocumentDefaults defaults = new WikiDocumentDefaults();
        setDefaults(defaults);
    }

    public WikiDocument(WikiDocumentDefaults defaults) {
        super(defaults.getName());
        setDefaults(defaults);
    }

    @Override
    @org.hibernate.search.annotations.Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    @Searchable(description = "Name")
    public String getName() {
        return super.getName();
    }

    public String getHeader() { return header; }
    public void setHeader(String header) { this.header = header; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getFooter() { return footer; }
    public void setFooter(String footer) { this.footer = footer; }

    public boolean isNameAsTitle() { return nameAsTitle; }
    public void setNameAsTitle(boolean nameAsTitle) { this.nameAsTitle = nameAsTitle; }

    public boolean isEnableComments() { return enableComments; }
    public void setEnableComments(boolean enableComments) { this.enableComments = enableComments; }

    public boolean isEnableCommentForm() { return enableCommentForm; }
    public void setEnableCommentForm(boolean enableCommentForm) { this.enableCommentForm = enableCommentForm; }

    public boolean isEnableCommentsOnFeeds() { return enableCommentsOnFeeds; }
    public void setEnableCommentsOnFeeds(boolean enableCommentsOnFeeds) { this.enableCommentsOnFeeds = enableCommentsOnFeeds; }

    public String getHeaderMacrosString() { return headerMacrosString; }
    public void setHeaderMacrosString(String headerMacrosString) { this.headerMacrosString = headerMacrosString; }

    public String getContentMacrosString() { return contentMacrosString; }
    public void setContentMacrosString(String contentMacrosString) { this.contentMacrosString = contentMacrosString; }

    public String getFooterMacrosString() { return footerMacrosString; }
    public void setFooterMacrosString(String footerMacrosString) { this.footerMacrosString = footerMacrosString; }

    public void flatCopy(WikiDocument original, boolean copyLazyProperties) {
        super.flatCopy(original, copyLazyProperties);
        this.nameAsTitle = original.nameAsTitle;
        this.enableComments = original.enableComments;
        this.enableCommentForm = original.enableCommentForm;
        this.enableCommentsOnFeeds = original.enableCommentsOnFeeds;
        this.headerMacrosString = original.headerMacrosString;
        this.contentMacrosString = original.contentMacrosString;
        this.footerMacrosString = original.footerMacrosString;
        this.header = original.header;
        this.footer = original.footer;
        if (copyLazyProperties) {
            this.content = original.content;
        }
    }

    public WikiDocument duplicate(boolean copyLazyProperties) {
        WikiDocument dupe = new WikiDocument();
        dupe.flatCopy(this, copyLazyProperties);
        return dupe;
    }

    public void rollback(WikiDocument revision) {
        super.rollback(revision);
        this.content = revision.content;
    }

    public String getFeedDescription() {
        return getContent();
    }

    public String getHistoricalEntityName() {
        return "HistoricalWikiDocument";
    }

    public String getPermURL(String suffix) {
        return getId() + suffix;
    }

    public String getWikiURL() {
        return getArea().getWikiname() + "/" + getWikiname();
    }

    public void setDefaults(WikiDocumentDefaults defaults) {
        setName(defaults.getName());
        content = defaults.getContentText() != null ? defaults.getContentText() : "";
        header = defaults.getHeaderText() != null ? defaults.getHeaderText() : null;
        footer = defaults.getFooterText() != null ? defaults.getFooterText() : null;
        setMacroFieldsFromDefaults(defaults);
        defaults.setOptions(this);
    }

    /*
        Macro handling routines, based on the following concept:

        - Persistent HEADER/CONTENT/FOOTER fields: This is the editable text, shown to the user.

        - Persistent HEADER_MACROS/CONTENT_MACROS/FOOTER_MACROS fields: These are strings that represent
          a space-separated list of all macros (names only) the user has entered. We need this separate
          in the database for aggregation queries, e.g. find me all documents that have "forumPosting"
          macro in the HEADER_MACROS field.

        - Transient headerMacros/contentMacros/footerMacros fields: These are type-safe collections
          that represent the macros of this document. They are empty after you load a document from
          the database and need to be generated. This generation is outside of the model class, we
          expect that the client who loads the document will parse the persistent fields and call
          the setters with filled collections. Only then will macroPresent(name) return a correct
          result.
     */

    @Transient
    private Collection<WikiTextMacro> headerMacros = new LinkedHashSet<WikiTextMacro>();
    public Collection<WikiTextMacro> getHeaderMacros() { return headerMacros; }
    public void setHeaderMacros(Collection<WikiTextMacro> headerMacros) {
        this.headerMacros = headerMacros;
        setHeaderMacrosString(getMacrosAsString(headerMacros));
        setHeader(getMacrosAsWikiText(headerMacros) + getWikiTextWithoutMacros(getHeader()) ); // Text after macros
    }

    @Transient
    private Collection<WikiTextMacro> contentMacros = new LinkedHashSet<WikiTextMacro>();
    public Collection<WikiTextMacro> getContentMacros() { return contentMacros; }
    public void setContentMacros(Collection<WikiTextMacro> contentMacros) {
        this.contentMacros = contentMacros;
        setContentMacrosString(getMacrosAsString(contentMacros));
    }

    @Transient
    private Collection<WikiTextMacro> footerMacros = new LinkedHashSet<WikiTextMacro>();
    public Collection<WikiTextMacro> getFooterMacros() { return footerMacros; }
    public void setFooterMacros(Collection<WikiTextMacro> footerMacros) {
        this.footerMacros = footerMacros;
        setFooterMacrosString(getMacrosAsString(footerMacros));
        setFooter(getWikiTextWithoutMacros(getFooter()) + getMacrosAsWikiText(footerMacros)); // Text before macros
    }

    public void addHeaderMacro(WikiTextMacro... macro) {
        headerMacros.addAll(Arrays.asList(macro));
        setHeaderMacros(headerMacros);
    }

    public void addFooterMacro(WikiTextMacro... macro) {
        footerMacros.addAll(Arrays.asList(macro));
        setFooterMacros(footerMacros);
    }

    public void removeHeaderMacro(WikiTextMacro... macro) {
        headerMacros.removeAll(Arrays.asList(macro));
        setHeaderMacros(headerMacros);
    }

    public void removeFooterMacro(WikiTextMacro... macro) {
        footerMacros.removeAll(Arrays.asList(macro));
        setFooterMacros(footerMacros);
    }

    public void removeHeaderMacros(String macroName) {
        removeMacrosFromCollection(headerMacros, macroName);
        setHeaderMacros(headerMacros);
    }

    public void removeFooterMacros(String macroName) {
        removeMacrosFromCollection(footerMacros, macroName);
        setFooterMacros(footerMacros);
    }

    public boolean macroPresent(String macroName) {
        for (WikiTextMacro headerMacro : headerMacros) {
            if (headerMacro.getName().equals(macroName)) return true;
        }
        for (WikiTextMacro contentMacro : contentMacros) {
            if (contentMacro.getName().equals(macroName)) return true;
        }
        for (WikiTextMacro footerMacro : footerMacros) {
            if (footerMacro.getName().equals(macroName)) return true;
        }
        return false;
    }

    private void removeMacrosFromCollection(Collection<WikiTextMacro> macros, String macroName) {
        Iterator<WikiTextMacro> it = macros.iterator();
        while (it.hasNext()) {
            WikiTextMacro WikiTextMacro = it.next();
            if (WikiTextMacro.getName().equals(macroName)) it.remove();
        }
    }

    private String getWikiTextWithoutMacros(String wikiText) {
        if (wikiText == null) return "";
        StringBuilder textWithoutMacro = new StringBuilder();
        String[] textLines = wikiText.split("\n");
        for (int i = 0; i < textLines.length; i++) {
            if (!textLines[i].startsWith("[<=")) {
                textWithoutMacro.append(textLines[i]);
                if (i < textLines.length-1) textWithoutMacro.append("\n");
            }
        }
        return textWithoutMacro.toString();
    }

    private String getMacrosAsString(Collection<WikiTextMacro> macros) {
        if (macros.size() == 0) return "";
        StringBuilder macrosString = new StringBuilder();
        for (WikiTextMacro m : macros) {
            macrosString.append(m.getName()).append(" ");
        }
        return macrosString.substring(0, macrosString.length() - 1);
    }

    private String getMacrosAsWikiText(Collection<WikiTextMacro> macros) {
        if (macros.size() == 0) return "";
        StringBuilder macrosString = new StringBuilder();
        for (WikiTextMacro m : macros) {
            macrosString.append("[<=").append(m.getName());
            for (Map.Entry<String, String> param : m.getParams().entrySet()) {
                macrosString.append("[").append(param.getKey()).append("=").append(param.getValue()).append("]");
            }
            macrosString.append("]\n");
        }
        return macrosString.toString();
    }

    private void setMacroFieldsFromDefaults(WikiDocumentDefaults defaults) {

        if (defaults.getContentMacros() != null) {
            setContentMacros(defaults.getContentMacros());
            setContentMacrosString(getMacrosAsString(defaults.getContentMacros()));
            content = getMacrosAsWikiText(defaults.getContentMacros()) + "\n" + content;
        } else {
            Collection<WikiTextMacro> macros = new ArrayList<WikiTextMacro>();
            int i = 0;
            for (String m : defaults.getContentMacrosAsString()) {
                macros.add(new WikiTextMacro(m, i++));
            }
            setContentMacros(macros);
            setContentMacrosString(getMacrosAsString(macros));
            content = getMacrosAsWikiText(macros) + "\n" + content;
        }

        if (defaults.getHeaderMacros() != null) {
            setHeaderMacros(defaults.getHeaderMacros());
        } else {
            int i = 0;
            for (String m : defaults.getHeaderMacrosAsString()) {
                addHeaderMacro(new WikiTextMacro(m, i++));
            }
        }

        if (defaults.getFooterMacros() != null) {
            setFooterMacros(defaults.getFooterMacros());
        } else {
            int i = 0;
            for (String m : defaults.getFooterMacrosAsString()) {
                addFooterMacro(new WikiTextMacro(m, i++));
            }
        }
    }

    // TODO: Again, the ugly Hibernate group by bug
    public static String[] getPropertiesForGroupingInQueries() {
        return new String[]{
            "id", "version", "parent", "rating",
            "areaNumber", "name", "wikiname", "createdBy", "createdOn", "messageId",
            "lastModifiedBy", "lastModifiedOn", "readAccessLevel", "writeAccessLevel", "writeProtected",
            "nameAsTitle", "enableComments", "enableCommentForm", "enableCommentsOnFeeds",
            "header", "headerMacrosString", "contentMacrosString", "footer", "footerMacrosString"
        };
    }

    public String toString() {
        return "WikiDocument (" + getId() + "): " + getName();
    }
}
