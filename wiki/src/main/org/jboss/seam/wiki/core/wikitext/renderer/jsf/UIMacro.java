package org.jboss.seam.wiki.core.wikitext.renderer.jsf;

import org.jboss.seam.wiki.core.plugin.WikiPluginMacro;

import javax.faces.component.UINamingContainer;

/**
 * A wrapper component that applies to macro includes.
 * <p>
 * A macro XHTML template must have a <tt>&lt;wiki:macro&gt;</tt> root element.
 * </p>
 *
 * @author Pete Muir
 */
public class UIMacro extends UINamingContainer {

    public static final String COMPONENT_FAMILY = "org.jboss.seam.wiki.core.ui.UIMacro";

    public static final String NEXT_MACRO = "org.jboss.seam.wiki.core.ui.UIMacro.nextMacro";

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    private WikiPluginMacro wikiPluginMacro;

    public WikiPluginMacro getWikiMacro() {
        return wikiPluginMacro;
    }

    public void setWikiMacro(WikiPluginMacro wikiPluginMacro) {
        this.wikiPluginMacro = wikiPluginMacro;
    }
}
