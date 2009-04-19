/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.wikitext.renderer.jsf;

import antlr.ANTLRException;
import antlr.RecognitionException;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.MetaRuleset;
import com.sun.facelets.tag.MetaTagHandler;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.TagConfig;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.wiki.core.wikitext.engine.WikiTextParser;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import java.io.IOException;
import java.util.Iterator;

/**
 * Creates a <tt>UIWikiFormattedText</tt> JSF component and substitutes macro names in wiki
 * text with real macro components in the tree. These <tt>UIMacro</tt> components are
 * build from XHTML fragments/includes. Interacts closely with the state of the
 * <tt>UIWikiFormattedText</tt> component to split component tree creation and rendering duties.
 *
 * @author Peter Muir
 * @author Christian Bauer
 */
public class WikiFormattedTextHandler extends MetaTagHandler {

    private Log log = Logging.getLog(WikiFormattedTextHandler.class);

    private static final String MARK = "org.jboss.seam.wiki.core.ui.WikiFormattedTextHandler";
    private TagAttribute valueAttribute;

    public WikiFormattedTextHandler(TagConfig config) {
        super(config);
        this.valueAttribute = this.getRequiredAttribute("value");
    }

    /*
    * Main apply method called by facelets to create this component.
    */
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException, FacesException, ELException {
        log.debug(">>> building wiki text components for child of: " + parent.getClientId(ctx.getFacesContext()));
        String id = ctx.generateUniqueId(this.tagId);
        UIComponent cmp = findChildByTagId(parent, id);
        if (cmp == null) {
            cmp = createComponent(ctx);
            cmp.getAttributes().put(MARK, id);
        }
        this.nextHandler.apply(ctx, cmp);
        parent.getChildren().add(cmp);
        createMacroComponents(ctx, cmp);
        log.debug("<<< completed building wiki text components for child of: " + parent.getClientId(ctx.getFacesContext()));
    }

    private UIComponent createComponent(FaceletContext ctx) {
        UIWikiFormattedText wikiFormattedText = new UIWikiFormattedText();
        setAttributes(ctx, wikiFormattedText);
        return wikiFormattedText;
    }

    /*
    * Have to manually wire the component as the Facelets magic wirer
    * is a package scoped class.
    */
    @Override
    protected void setAttributes(FaceletContext ctx, Object instance) {
        UIComponent cmp = (UIComponent) instance;
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_LINK_STYLE_CLASS);
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_BROKEN_LINK_STYLE_CLASS);
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_ATTACHMENT_LINK_STYLE_CLASS);
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_THUMBNAIL_LINK_STYLE_CLASS);
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_INTERNAL_TARGET_FRAME);
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_EXTERNAL_TARGET_FRAME);
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_LINK_BASE_FILE);
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_CURRENT_AREA_NUMBER);
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_ENABLE_MACRO_RENDERING, false);
        setAttribute(ctx, cmp, UIWikiFormattedText.ATTR_ENABLE_TRANSIENT_MACROS, false);
    }

    @Override
    protected MetaRuleset createMetaRuleset(Class type) {
        return super.createMetaRuleset(type).ignoreAll();
    }

    private void createMacroComponents(final FaceletContext ctx, final UIComponent parent) {
        if (!(parent instanceof UIWikiFormattedText)) return;
        final UIWikiFormattedText wikiFormattedTextComponent = (UIWikiFormattedText) parent;

        String unparsed = valueAttribute.getValue(ctx);

        // Don't forget this, transporting the value to the handled component, we need to render it (again) later
        wikiFormattedTextComponent.setValue(unparsed);

        if (getAttribute(UIWikiFormattedText.ATTR_ENABLE_MACRO_RENDERING) == null ||
            !getAttribute(UIWikiFormattedText.ATTR_ENABLE_MACRO_RENDERING).getBoolean(ctx)) {
            log.debug("macro rendering disabled");
            return;
        }

        // We need to parse the wiki text once (later again for rendering) to find all macros in the text
        log.debug("creating macro components from wiki text macros");
        WikiTextParser parser = new WikiTextParser(unparsed, true, false);

        // This is a special flag that disables/enables caching of ValueExpressions inside macro templates,
        // see the MacroIncludeTextRenderer for an explanation...
        boolean enableTransientMacros =
            (getAttribute(UIWikiFormattedText.ATTR_ENABLE_TRANSIENT_MACROS) != null &&
             getAttribute(UIWikiFormattedText.ATTR_ENABLE_TRANSIENT_MACROS).getBoolean(ctx));

        // We use a renderer to implement the callbacks from the parsing and to create the components in the tree
        parser.setRenderer(
            new MacroIncludeTextRenderer(wikiFormattedTextComponent, ctx, enableTransientMacros)
        );

        try {
            parser.parse();
        } catch (RecognitionException rex) {
            // Swallow parsing errors, we don't really care here...
        } catch (ANTLRException ex) {
            // All other errors are fatal;
            throw new RuntimeException(ex);
        }
    }

    // Some utilities...

    private static UIComponent findChildByTagId(UIComponent parent, String id) {
        Iterator itr = parent.getFacetsAndChildren();
        while (itr.hasNext()) {
            UIComponent c = (UIComponent) itr.next();
            String cid = (String) c.getAttributes().get(MARK);
            if (id.equals(cid)) {
                return c;
            }
        }
        return null;
    }

    private void setAttribute(FaceletContext ctx, UIComponent cmp, String name) {
        setAttribute(ctx, cmp, name, null);
    }

    private void setAttribute(FaceletContext ctx, UIComponent cmp, String name, Object defaultValue) {
        TagAttribute attribute = this.getAttribute(name);
        if (attribute != null) {
            Object o = attribute.getObject(ctx);
            if (o == null && defaultValue == null) {
                throw new IllegalArgumentException("Attribute '" + name + "' resolved to null and no default value specified");
            } else if (o == null) {
                cmp.getAttributes().put(name, defaultValue);
            } else {
                cmp.getAttributes().put(name, o);
            }
        }
    }


}