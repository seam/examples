package org.jboss.seam.wiki.core.wikitext.renderer;

import org.jboss.seam.wiki.core.model.WikiTextMacro;
import org.jboss.seam.wiki.core.wikitext.engine.WikiLink;

import java.util.List;

/**
 * Called by the WikiTextParser to render [A Link=>Target] and [<=MacroName].
 *
 * @author Christian Bauer
 */
public interface WikiTextRenderer {

    public static final String HEADLINE_ID_PREFIX = "H-";

    public String renderInternalLink(WikiLink internalLink);
    public String renderExternalLink(WikiLink externalLink);
    public String renderThumbnailImageLink(WikiLink link);
    public String renderFileAttachmentLink(int attachmentNumber, WikiLink attachmentLink);
    public String renderMacro(WikiTextMacro macro);

    public String renderParagraphOpenTag();
    public String renderPreformattedOpenTag();
    public String renderBlockquoteOpenTag();
    public String renderHeadline1(String headline);
    public String renderHeadline2(String headline);
    public String renderHeadline3(String headline);
    public String renderHeadline4(String headline);
    public String renderOrderedListOpenTag();
    public String renderOrderedListItemOpenTag();
    public String renderUnorderedListOpenTag();
    public String renderUnorderedListItemOpenTag();
    public String renderEmphasisOpenTag();
    public String renderEmphasisCloseTag();

    public void setAttachmentLinks(List<WikiLink> attachmentLinks);
    public void setExternalLinks(List<WikiLink> externalLinks);
}
