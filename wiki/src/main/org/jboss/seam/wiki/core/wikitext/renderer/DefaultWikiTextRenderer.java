package org.jboss.seam.wiki.core.wikitext.renderer;

import org.jboss.seam.wiki.core.wikitext.renderer.WikiTextRenderer;
import org.jboss.seam.wiki.core.wikitext.engine.WikiLink;
import org.jboss.seam.wiki.core.model.WikiTextMacro;
import org.jboss.seam.wiki.core.ui.WikiURLRenderer;
import org.jboss.seam.Component;

import java.util.List;

/**
 * Convenience class that renders some sensible defaults that apply for the wiki.
 *
 * @author Christian Bauer
 */
public class DefaultWikiTextRenderer implements WikiTextRenderer {

    protected WikiURLRenderer wikiURLRenderer = (WikiURLRenderer) Component.getInstance(WikiURLRenderer.class);

    public static enum Headline {
        H1, H2, H3, H4
    }

    public String renderInternalLink(WikiLink internalLink) {
        return !internalLink.isBroken() ?
                "<a href=\""
                + wikiURLRenderer.renderURL(internalLink.getFile())
                + "\">"
                + internalLink.getDescription()
                + "</a>" : "[Broken Link]";
    }

    public String renderExternalLink(WikiLink externalLink) {
        return "<a href=\""
                + externalLink.getUrl()
                + "\">"
                + externalLink.getDescription()
                + "</a>";
    }

    public String renderFileAttachmentLink(int attachmentNumber, WikiLink attachmentLink) {
        return "[Attachment]";
    }

    public String renderThumbnailImageLink(WikiLink link) {
        return "[Embedded Image]";
    }

    public String renderMacro(WikiTextMacro macro) {
        return "[Macro]";
    }

    public void setAttachmentLinks(List<WikiLink> attachmentLinks) {}
    public void setExternalLinks(List<WikiLink> externalLinks) {}

    public String renderParagraphOpenTag() {
        return "<p class=\"wikiPara\">\n";
    }

    public String renderPreformattedOpenTag() {
        return "<pre class=\"wikiPreformatted\">\n";
    }

    public String renderBlockquoteOpenTag() {
        return "<blockquote class=\"wikiBlockquote\">\n";
    }

    public String renderHeadline1(String headline) {
        return "<h1 class=\"wikiHeadline1\" id=\""+getHeadlineId(Headline.H1, headline)+"\">"
                + getHeadlineLink(Headline.H1, headline)
               + "</h1>";
    }

    public String renderHeadline2(String headline) {
        return "<h2 class=\"wikiHeadline2\" id=\""+getHeadlineId(Headline.H2, headline)+"\">"
                + getHeadlineLink(Headline.H2, headline)
               + "</h2>";
    }

    public String renderHeadline3(String headline) {
        return "<h3 class=\"wikiHeadline3\" id=\""+getHeadlineId(Headline.H3, headline)+"\">"
                + getHeadlineLink(Headline.H3, headline)
               + "</h3>";
    }

    public String renderHeadline4(String headline) {
        return "<h4 class=\"wikiHeadline4\" id=\""+getHeadlineId(Headline.H4, headline)+"\">"
                + getHeadlineLink(Headline.H4, headline)
               + "</h4>";
    }


    public String renderOrderedListOpenTag() {
        return "<ol class=\"wikiOrderedList\">\n";
    }

    public String renderOrderedListItemOpenTag() {
        return "<li class=\"wikiOrderedListItem\">";
    }

    public String renderUnorderedListOpenTag() {
        return "<ul class=\"wikiUnorderedList\">\n";
    }

    public String renderUnorderedListItemOpenTag() {
        return "<li class=\"wikiUnorderedListItem\">";
    }

    public String renderEmphasisOpenTag() {
        return "<i class=\"wikiEmphasis\">";
    }

    public String renderEmphasisCloseTag() {
        return "</i>";
    }

    protected String getHeadlineId(Headline h, String headline) {
        return "";
    }

    protected String getHeadlineLink(Headline h, String headline) {
        return headline;
    }
}
