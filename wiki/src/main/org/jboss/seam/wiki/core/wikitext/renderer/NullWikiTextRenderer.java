package org.jboss.seam.wiki.core.wikitext.renderer;

import org.jboss.seam.wiki.core.wikitext.renderer.WikiTextRenderer;
import org.jboss.seam.wiki.core.wikitext.engine.WikiLink;
import org.jboss.seam.wiki.core.model.WikiTextMacro;

import java.util.List;

/**
 * Renders nothing for links and macros and unstyled HTML for anything else.
 *
 * @author Christian Bauer
 */
public class NullWikiTextRenderer implements WikiTextRenderer {

    public String renderInternalLink(WikiLink internalLink) { return null; }
    public String renderExternalLink(WikiLink externalLink) { return null; }
    public String renderFileAttachmentLink(int attachmentNumber, WikiLink attachmentLink) { return null; }
    public String renderThumbnailImageLink(WikiLink link) { return null; }
    public void setAttachmentLinks(List<WikiLink> attachmentLinks) {}
    public void setExternalLinks(List<WikiLink> externalLinks) {}
    public String renderMacro(WikiTextMacro macro) { return null; }
    public String renderParagraphOpenTag() { return "<p>\n"; }
    public String renderPreformattedOpenTag() { return "<pre>\n"; }
    public String renderBlockquoteOpenTag() { return "<blockquote>\n"; }
    public String renderHeadline1(String headline) { return "<h1>"+headline+"</h1>"; }
    public String renderHeadline2(String headline) { return "<h2>"+headline+"</h2>"; }
    public String renderHeadline3(String headline) { return "<h3>"+headline+"</h3>"; }
    public String renderHeadline4(String headline) { return "<h4>"+headline+"</h4>"; }
    public String renderOrderedListOpenTag() { return "<ol>\n"; }
    public String renderOrderedListItemOpenTag() { return "<li>"; }
    public String renderUnorderedListOpenTag() { return "<ul>\n"; }
    public String renderUnorderedListItemOpenTag() { return "<li>"; }
    public String renderEmphasisOpenTag() { return "<i>"; }
    public String renderEmphasisCloseTag() { return "</i>"; }

}
