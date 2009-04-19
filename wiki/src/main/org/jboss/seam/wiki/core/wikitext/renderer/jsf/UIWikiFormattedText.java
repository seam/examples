/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.wikitext.renderer.jsf;

import antlr.ANTLRException;
import antlr.RecognitionException;
import org.jboss.seam.Component;
import org.jboss.seam.core.Events;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.ui.util.JSF;
import org.jboss.seam.wiki.core.model.WikiFile;
import org.jboss.seam.wiki.core.model.WikiUploadImage;
import org.jboss.seam.wiki.core.model.WikiTextMacro;
import org.jboss.seam.wiki.core.wikitext.renderer.DefaultWikiTextRenderer;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.wiki.core.plugin.WikiPluginMacro;
import org.jboss.seam.wiki.core.wikitext.editor.WikiFormattedTextValidator;
import org.jboss.seam.wiki.core.wikitext.engine.WikiLink;
import org.jboss.seam.wiki.core.wikitext.engine.WikiLinkResolver;
import org.jboss.seam.wiki.core.wikitext.engine.WikiTextParser;
import org.jboss.seam.wiki.core.wikitext.renderer.WikiTextRenderer;

import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Uses WikiTextParser and WikiLinkResolver to render Seam Text markup with wiki links.
 *
 * <p>
 * Any lexer/parser error results in WARN level log message, you can disable this in your logging
 * configuration by raising the log level for this class to ERROR.
 * </p>
 *
 * @author Christian Bauer
 */
public class UIWikiFormattedText extends UIOutput {

    Log log = Logging.getLog(UIWikiFormattedText.class);

    public static final String ATTR_LINK_STYLE_CLASS                = "linkStyleClass";
    public static final String ATTR_BROKEN_LINK_STYLE_CLASS         = "brokenLinkStyleClass";
    public static final String ATTR_ATTACHMENT_LINK_STYLE_CLASS     = "attachmentLinkStyleClass";
    public static final String ATTR_THUMBNAIL_LINK_STYLE_CLASS      = "thumbnailLinkStyleClass";
    public static final String ATTR_INTERNAL_TARGET_FRAME           = "internalTargetFrame";
    public static final String ATTR_EXTERNAL_TARGET_FRAME           = "externalTargetFrame";
    public static final String ATTR_LINK_BASE_FILE                  = "linkBaseFile";
    public static final String ATTR_CURRENT_AREA_NUMBER             = "currentAreaNumber";
    public static final String ATTR_ENABLE_MACRO_RENDERING          = "enableMacroRendering";
    public static final String ATTR_ENABLE_TRANSIENT_MACROS         = "enableTransientMacros";

    private Map<Integer, WikiPluginMacro> macrosWithTemplateByPosition;

    public static final String COMPONENT_FAMILY = "org.jboss.seam.wiki.core.ui.UIWikiFormattedText";

    public static final String COMPONENT_TYPE = "org.jboss.seam.wiki.core.ui.UIWikiFormattedText";

    public UIWikiFormattedText() {
        super();
        macrosWithTemplateByPosition = new HashMap<Integer, WikiPluginMacro>();
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public String getRendererType() {
        return null;
    }

    @Override
    public void encodeBegin(FacesContext facesContext) throws IOException {
        if (!isRendered() || getValue() == null) return;
        log.debug(">>> ENCODE BEGIN of WikiFormattedText component");

        // Use the WikiTextParser to resolve macros
        WikiTextParser parser = new WikiTextParser((String) getValue(), true, true);

        // Resolve the base document and directory we are resolving against
        final WikiFile baseFile = (WikiFile)getAttributes().get(ATTR_LINK_BASE_FILE);
        final Long currentAreaNumber = (Long)getAttributes().get(ATTR_CURRENT_AREA_NUMBER);
        parser.setCurrentAreaNumber(currentAreaNumber);

        parser.setResolver((WikiLinkResolver)Component.getInstance("wikiLinkResolver"));

        // TODO: Externalize this to separate class, extensible
        // Set a customized renderer for parser macro callbacks
        class WikiFormattedTextRenderer extends DefaultWikiTextRenderer {

            @Override
            public String renderInternalLink(WikiLink internalLink) {
                return "<a href=\""
                        + (
                            internalLink.isBroken()
                                ? internalLink.getUrl()
                                : wikiURLRenderer.renderURL(internalLink.getFile())
                           )
                        + (
                            internalLink.getFragment() != null
                                ? "#"+internalLink.getEncodedFragment()
                                : ""
                          )
                        + "\" target=\""
                        + (getAttributes().get(ATTR_INTERNAL_TARGET_FRAME) != null ? getAttributes().get(ATTR_INTERNAL_TARGET_FRAME) : "")
                        + "\" class=\""
                        + (internalLink.isBroken() ? getAttributes().get(ATTR_BROKEN_LINK_STYLE_CLASS)
                        : getAttributes().get(ATTR_LINK_STYLE_CLASS)) + "\">"
                        + internalLink.getDescription() + "</a>";
            }

            @Override
            public String renderExternalLink(WikiLink externalLink) {
                return "<a href=\""
                        + WikiUtil.escapeEmailURL(externalLink.getUrl())
                        + "\" target=\""
                        + (getAttributes().get(ATTR_EXTERNAL_TARGET_FRAME) != null ? getAttributes().get(ATTR_EXTERNAL_TARGET_FRAME) : "")
                        + "\" class=\""
                        + (externalLink.isBroken() ? getAttributes().get(ATTR_BROKEN_LINK_STYLE_CLASS)
                        : getAttributes().get(ATTR_LINK_STYLE_CLASS)) + "\">"
                        + WikiUtil.escapeEmailURL(externalLink.getDescription()) + "</a>";
            }

            @Override
            public String renderFileAttachmentLink(int attachmentNumber, WikiLink attachmentLink) {
                return "<a href=\""
                        + wikiURLRenderer.renderURL(baseFile)
                        + "#attachment" + attachmentNumber
                        + "\" target=\""
                        + (getAttributes().get(ATTR_INTERNAL_TARGET_FRAME) != null ? getAttributes().get(ATTR_INTERNAL_TARGET_FRAME) : "")
                        + "\" class=\""
                        + getAttributes().get(ATTR_ATTACHMENT_LINK_STYLE_CLASS) + "\">"
                        + attachmentLink.getDescription() + "[" + attachmentNumber + "]" + "</a>";
            }

            @Override
            public String renderThumbnailImageLink(WikiLink link) {

                // TODO: This is not typesafe and clean, need different rendering strategy for WikiUpload subclasses
                WikiUploadImage image = (WikiUploadImage)link.getFile();
                if (image.getThumbnail() == WikiUploadImage.Thumbnail.FULL.getFlag()) {
                    // Full size display, no thumbnail
                    //TODO: Make sure we really don't need this - but it messes up the comment form conversation:
                    //String imageUrl = WikiUtil.renderURL(image) + "&amp;cid=" + Conversation.instance().getId();
                    String imageUrl = wikiURLRenderer.renderURL(image);
                    return "<img src='"+ imageUrl + "'" +
                            " width='"+ image.getSizeX()+"'" +
                            " height='"+ image.getSizeY() +"'/>";
                } else {
                    // Thumbnail with link display

                    //TODO: Make sure we really don't need this - but it messes up the comment form conversation:
                    // String thumbnailUrl = WikiUtil.renderURL(image) + "&amp;thumbnail=true&amp;cid=" + Conversation.instance().getId();
                    String thumbnailUrl = wikiURLRenderer.renderURL(image) + "?thumbnail=true";

                    return "<a href=\""
                            + (link.isBroken() ? link.getUrl() : wikiURLRenderer.renderURL(image))
                            + "\" target=\""
                            + (getAttributes().get(ATTR_INTERNAL_TARGET_FRAME) != null ? getAttributes().get(ATTR_INTERNAL_TARGET_FRAME) : "")
                            + "\" class=\""
                            + getAttributes().get(ATTR_THUMBNAIL_LINK_STYLE_CLASS) + "\"><img src=\""
                            + thumbnailUrl + "\"/></a>";
                }
            }

            @Override
            public String renderMacro(WikiTextMacro macro) {

                WikiPluginMacro pluginMacroWithTemplate = macrosWithTemplateByPosition.get(macro.getPosition());
                if (pluginMacroWithTemplate == null) {
                    log.debug("macro does not have an XHTML template/include, skipping: " + macro);
                    return "";
                }

                log.debug("firing BEFORE_VIEW_RENDER macro event");
                Events.instance().raiseEvent(
                    pluginMacroWithTemplate.getCallbackEventName(WikiPluginMacro.CallbackEvent.BEFORE_VIEW_RENDER),
                        pluginMacroWithTemplate
                );

                log.debug("preparing include rendering for macro: " + pluginMacroWithTemplate);
                UIComponent child = findComponent( pluginMacroWithTemplate.getClientId() );
                log.debug("JSF child client identifier: " + child.getClientId(getFacesContext()));
                ResponseWriter originalResponseWriter = getFacesContext().getResponseWriter();
                StringWriter stringWriter = new StringWriter();
                ResponseWriter tempResponseWriter = originalResponseWriter
                        .cloneWithWriter(stringWriter);
                getFacesContext().setResponseWriter(tempResponseWriter);

                try {
                    log.debug("rendering template of macro: " + pluginMacroWithTemplate);
                    JSF.renderChild(getFacesContext(), child);

                    log.debug("firing AFTER_VIEW_RENDER macro event");
                    Events.instance().raiseEvent(
                        pluginMacroWithTemplate.getCallbackEventName(WikiPluginMacro.CallbackEvent.AFTER_VIEW_RENDER),
                        pluginMacroWithTemplate
                    );
                }
                catch (Exception ex) {
                    throw new RuntimeException(ex);
                } finally {
                    getFacesContext().setResponseWriter(originalResponseWriter);
                }
                return stringWriter.getBuffer().toString();
            }

            @Override
            public void setAttachmentLinks(List<WikiLink> attachmentLinks) {
                // Put attachments (wiki links...) into the event context for later rendering
                setLinks("wikiTextAttachments", attachmentLinks);
            }

            @Override
            public void setExternalLinks(List<WikiLink> externalLinks) {
                // Put external links (to targets not on this wiki) into the event context for later rendering
                setLinks("wikiTextExternalLinks", externalLinks);
            }

            private void setLinks(String contextVariable, List<WikiLink> links) {
                // TODO: Need some tricks here with link identifiers and attachment numbers, right now we just skip this if it's already set
                /// ... hoping that the first caller was the document renderer and not the comment renderer - that means comment attachments are broken
                List<WikiLink> contextLinks = (List<WikiLink>)Contexts.getEventContext().get(contextVariable);
                if (contextLinks == null || contextLinks.size()==0) {
                    Contexts.getEventContext().set(contextVariable, links);
                }
                        /*
                Map<Integer, WikiLink> contextLinks =
                    (Map<Integer,WikiLink>)Contexts.getEventContext().get(contextVariable);
                if (contextLinks == null) {
                    contextLinks = new HashMap<Integer, WikiLink>();
                }
                for (WikiLink link : links) {
                    contextLinks.put(link.getIdentifier(), link);
                }
                Contexts.getEventContext().set(contextVariable, contextLinks);
                */
            }

            @Override
            protected String getHeadlineId(Headline h, String headline) {
                // HTML id attribute has restrictions on valid values... so the easiest way is to make this a WikiLink
                return HEADLINE_ID_PREFIX+WikiUtil.convertToWikiName(headline);
                // We also need to access it correctly, see WikiLink.java and getHeadLineLink()
            }

            @Override
            protected String getHeadlineLink(Headline h, String headline) {
                return "<a href=\""+ wikiURLRenderer.renderURL(baseFile)+"#"+ WikiTextRenderer.HEADLINE_ID_PREFIX+WikiUtil.convertToWikiName(headline)+"\">"
                        + headline
                       +"</a>";
            }
        }

        parser.setRenderer(new WikiFormattedTextRenderer());

        try {
            log.debug("parsing wiki text for HTML encoding");
            parser.parse();

        } catch (RecognitionException rex) {
            // Log a nice message for any lexer/parser errors, users can disable this if they want to
            log.warn( WikiFormattedTextValidator.getErrorMessage((String) getValue(), rex) );
        } catch (ANTLRException ex) {
            // All other errors are fatal;
            throw new RuntimeException(ex);
        }

        facesContext.getResponseWriter().write(parser.toString());

        log.debug("<<< ENCODE END of WikiFormattedText component");
    }

    protected void addMacroWithTemplate(WikiPluginMacro pluginMacro) {
        macrosWithTemplateByPosition.put(pluginMacro.getPosition(), pluginMacro);
    }

}
