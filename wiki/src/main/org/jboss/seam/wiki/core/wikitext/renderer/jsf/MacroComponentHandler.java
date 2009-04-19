package org.jboss.seam.wiki.core.wikitext.renderer.jsf;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.tag.jsf.ComponentConfig;
import com.sun.facelets.tag.jsf.ComponentHandler;

import javax.faces.component.UIComponent;

/**
 * Chaining up the macros in the JSF component tree.
 *
 * @author Pete Muir
 */
public class MacroComponentHandler extends ComponentHandler {

    public MacroComponentHandler(ComponentConfig config) {
        super(config);
    }

    @Override
    protected void onComponentCreated(FaceletContext ctx, UIComponent c, UIComponent parent) {
        super.onComponentCreated(ctx, c, parent);
        parent.getAttributes().put(UIMacro.NEXT_MACRO, c.getClientId(ctx.getFacesContext()));
    }

}
