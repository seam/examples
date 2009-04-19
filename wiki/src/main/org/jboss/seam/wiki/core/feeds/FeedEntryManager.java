/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.feeds;

import antlr.ANTLRException;
import antlr.RecognitionException;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.wikitext.engine.WikiLink;
import org.jboss.seam.wiki.core.wikitext.engine.WikiLinkResolver;
import org.jboss.seam.wiki.core.wikitext.engine.WikiTextParser;
import org.jboss.seam.wiki.core.wikitext.editor.WikiFormattedTextValidator;
import org.jboss.seam.wiki.core.wikitext.renderer.DefaultWikiTextRenderer;
import org.jboss.seam.wiki.core.model.FeedEntry;
import org.jboss.seam.wiki.core.model.WikiTextMacro;
import org.jboss.seam.wiki.core.ui.WikiURLRenderer;
import org.jboss.seam.wiki.util.WikiUtil;

/**
 * @author Christian Bauer
 */
@Scope(ScopeType.APPLICATION)
public abstract class FeedEntryManager<M, FE extends FeedEntry> {

    @Logger
    static Log log;

    @In
    protected WikiURLRenderer wikiURLRenderer;

    public abstract FE createFeedEntry(M source);
    public abstract void updateFeedEntry(FE feedEntry, M source);
    public abstract String getFeedEntryTitle(M source);

    protected String renderWikiText(Long currentAreaNumber, String wikiText) {
        WikiTextParser parser = new WikiTextParser(wikiText, true, true);

        parser.setCurrentAreaNumber(currentAreaNumber);
        parser.setResolver((WikiLinkResolver) Component.getInstance("wikiLinkResolver"));

        class FeedRenderer extends DefaultWikiTextRenderer {
            @Override
            public String renderInternalLink(WikiLink internalLink) {
                return !internalLink.isBroken() ?
                        "<a href=\""
                        + wikiURLRenderer.renderURL(internalLink.getFile())
                        + "\">"
                        + internalLink.getDescription()
                        + "</a>" : "[Broken Link]";
            }

            // Remove all macros
            @Override
            public String renderMacro(WikiTextMacro macro) {
                return "";
            }
        }
        parser.setRenderer( new FeedRenderer() );

        // Run the parser
        try {
            parser.parse();

        } catch (RecognitionException rex) {
            // Swallow and log and low debug level
            log.debug( "Ignored parse error generating feed entry text: " + WikiFormattedTextValidator.getErrorMessage(wikiText, rex) );
        } catch (ANTLRException ex) {
            // All other errors are fatal;
            throw new RuntimeException(ex);
        }
        return parser.toString();
    }

    protected String renderPlainText(String plainText) {
        return WikiUtil.escapeHtml(plainText, true, true);
    }


}
