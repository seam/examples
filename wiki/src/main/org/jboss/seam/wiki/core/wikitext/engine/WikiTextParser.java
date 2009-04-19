/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.wikitext.engine;

import antlr.ANTLRException;
import antlr.SemanticException;
import antlr.Token;
import org.jboss.seam.text.SeamTextLexer;
import org.jboss.seam.text.SeamTextParser;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.wikitext.renderer.WikiTextRenderer;

import java.io.StringReader;
import java.util.*;

/**
 * Parses SeamText markup and also resolves link and macro tags as wiki links and wiki plugins.
 * <p>
 * Don't forget to set the resolver and renderer base with <tt>setCurrentAreaNumber()</tt>!
 * </p><p>
 * Picks the <tt>WikiLinkResolver</tt> present in the contextual variable <tt>wikiLinkResolver</tt>. Calls
 * out to a <tt>WikiTextRender</tt> for the actual in-document rendering of wiki links and wiki plugins. Might update
 * the <tt>currentDocument</tt>'s content, this change should be flushed to the datastore after calling
 * the parser.
 * </p><p>
 * After parsing, all links to attachments and all external links are pushed onto the renderer, where they
 * can be used to render an attachment list or appendixes to the text.
 * </p>
 *
 * @see org.jboss.seam.wiki.core.wikitext.renderer.WikiTextRenderer
 *
 * @author Christian Bauer
 */
public class WikiTextParser extends SeamTextParser {

    private int macroPosition = 0;
    private int linkCounter = 0;

    private WikiTextRenderer renderer;

    private boolean resolveLinks;
    private WikiLinkResolver resolver;
    private Long currentAreaNumber;

    private Map<String, WikiLink> resolvedLinks = new HashMap<String, WikiLink>();
    private List<WikiLink> attachments = new ArrayList<WikiLink>();
    private List<WikiLink> externalLinks = new ArrayList<WikiLink>();
    private Set<String> macroNames = new HashSet<String>();
    private boolean renderDuplicateMacros;

    public WikiTextParser(String wikiText, boolean renderDuplicateMacros, boolean resolveLinks) {
        super(new SeamTextLexer(new StringReader(wikiText)));
        this.renderDuplicateMacros = renderDuplicateMacros;
        this.resolveLinks = resolveLinks;

        setSanitizer(
            new DefaultSanitizer() {
                @Override
                public void validateLinkTagURI(Token token, String s) throws SemanticException {
                    // NOOP, we validate that later in linkTag()
                }
            }
        );
    }

    /**
     * Mandatory, you need to set a renderer before starting the parer.
     *
     * @param renderer an implementation of WikiTextRenderer
     * @return the called instance
     */
    public WikiTextParser setRenderer(WikiTextRenderer renderer) {
        this.renderer = renderer;
        return this;
    }

    /**
     * Mandatory, you need to set a resolver before starting the parer.
     *
     * @param resolver an implementation of WikiLinkresolver
     * @return the called instance
     */
    public WikiTextParser setResolver(WikiLinkResolver resolver) {
        this.resolver = resolver;
        return this;
    }

    /*
     * The render/link resolving base
     * @return the called instance
     */

    public void setCurrentAreaNumber(Long currentAreaNumber) {
        this.currentAreaNumber = currentAreaNumber;
    }

    /**
     * Start parsing the wiki text and resolve wiki links and wiki plugins.
     * <p>
     * @throws ANTLRException if lexer or parser errors occur, see
     */
    public void parse() throws ANTLRException {
        if (renderer == null) throw new IllegalStateException("WikiTextParser requires not null setRenderer()");

        if (resolveLinks) {
            if (resolver == null) throw new IllegalStateException("WikiTextParser requires not null setResolver()");
            if (currentAreaNumber == null) throw new IllegalStateException("WikiTextParser requires not null setCurrentAreaNumber()");
        }

        startRule();

        renderer.setAttachmentLinks(attachments);
        renderer.setExternalLinks(externalLinks);
    }

    protected String linkTag(String descriptionText, String linkText) {
        if (!resolveLinks) {
            // Don't resolve links, just call back to renderer for simple inline rendering of what we have
            WikiLink unresolvedLink = new WikiLink(false, false);
            unresolvedLink.setDescription(descriptionText);
            unresolvedLink.setUrl(linkText);
            return renderer.renderInternalLink(unresolvedLink);
        }

        resolver.resolveLinkText(currentAreaNumber, resolvedLinks, linkText);
        WikiLink link = resolvedLinks.get((linkText));
        if (link == null) return "";

        // Set an internal identifier, used for attachments and external links we later push into a hashmap into the contexts
        link.setIdentifier(linkCounter++);

        // Override the description of the WikiLink with description found in tag
        String finalDescriptionText =
                (descriptionText!=null && descriptionText.length() > 0 ? descriptionText : link.getDescription());
        link.setDescription(finalDescriptionText);

        // Link to upload (inline or attached)
        if (link.getFile() != null && link.getFile().isInstance(WikiUpload.class)) {
            WikiUpload upload = (WikiUpload)link.getFile();
            if (upload.isAttachedToDocuments()) {
                if (!attachments.contains(link)) {
                    attachments.add(link);
                }
                return renderer.renderFileAttachmentLink((attachments.indexOf(link)+1), link);
            } else {
                return renderer.renderThumbnailImageLink(link);
            }
        }

        // External link
        if (link.isExternal()) {
            if (!externalLinks.contains(link)) externalLinks.add(link);
            return renderer.renderExternalLink(link);
        }

        // Regular link
        return renderer.renderInternalLink(link);
    }

    protected String paragraphOpenTag() {
        return renderer.renderParagraphOpenTag();
    }

    protected String preformattedOpenTag() {
        return renderer.renderPreformattedOpenTag();
    }

    protected String blockquoteOpenTag() {
        return renderer.renderBlockquoteOpenTag();
    }

    protected String headline1(String headline) {
        return renderer.renderHeadline1(headline);
    }

    protected String headline2(String headline) {
        return renderer.renderHeadline2(headline);
    }

    protected String headline3(String headline) {
        return renderer.renderHeadline3(headline);
    }

    protected String headline4(String headline) {
        return renderer.renderHeadline4(headline);
    }

    protected String orderedListOpenTag() {
        return renderer.renderOrderedListOpenTag();
    }

    protected String orderedListItemOpenTag() {
        return renderer.renderOrderedListItemOpenTag();
    }

    protected String unorderedListOpenTag() {
        return renderer.renderUnorderedListOpenTag();
    }

    protected String unorderedListItemOpenTag() {
        return renderer.renderUnorderedListItemOpenTag();
    }

    protected String emphasisOpenTag() {
        return renderer.renderEmphasisOpenTag();
    }

    protected String emphasisCloseTag() {
        return renderer.renderEmphasisCloseTag();
    }

    protected String macroInclude(Macro macro) {
        if (macro.name == null || macro.name.length() == 0) return "";

        if ( (macroNames.contains(macro.name) && renderDuplicateMacros) || !macroNames.contains(macro.name)) {
            macroNames.add(macro.name);

            WikiTextMacro wikiTextMacro = new WikiTextMacro(macro.name, macroPosition++);
            wikiTextMacro.setParams(macro.params);

            return renderer.renderMacro(wikiTextMacro);
        } else {
            macroPosition++;
            return "[Can't use the same macro twice!]";
        }
    }

}
