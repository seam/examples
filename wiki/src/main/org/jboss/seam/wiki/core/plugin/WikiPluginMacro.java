/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.plugin;

import org.jboss.seam.Component;
import org.jboss.seam.web.ServletContexts;
import org.jboss.seam.international.Messages;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiTextMacro;
import org.jboss.seam.wiki.core.plugin.metamodel.MacroPluginModule;
import org.jboss.seam.wiki.util.Hash;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

/**
 * An instance of a macro in wiki text that has an XHTML include/template.
 * <p>
 * Internally it ties the wiki text parsing engine to the plugin and
 * preferences functionality, as well as template/UI rendering. It is also
 * the primary API for plugin developers, it's the 'currentMacro' that is
 * available at all times when writing plugin templates, plugin classes, or
 * interpolated plugin resources (CSS that contains EL expressions, etc.).
 * </p>
 * <p>
 * It extends a <tt>WikiTextMacro</tt> with additional features that are only
 * relevant for rendering the macro and its template.
 * </p>
 * <p>
 * A new instance of this class is created for <i>every</i> rendering of wiki
 * text in the wiki user interface (the visible GUI). You can use the
 * <tt>attributes</tt> map of this instance to store values that you want to
 * retrieve again during such rendering. This is especially useful if you
 * want to avoid generating or loading a value again and again during rendering
 * of a single piece of wiki text, which can occur if a JSF value binding method
 * (a getter) is generating/loading a value based on the macro instance.
 * </p>
 * <p>
 * An instance of this class can only be created by the <tt>PluginRegistry</tt>.
 *
 * @see PluginRegistry
 * 
 * @author Christian Bauer
 */
public class WikiPluginMacro extends WikiTextMacro implements Serializable {

    private Log log = Logging.getLog(WikiPluginMacro.class);

    public static final String CURRENT_MACRO_EL_VARIABLE = "currentMacro";
    public static final String PAGE_VARIABLE_PREFIX = "macro";
    public static final String PAGE_VARIABLE_SEPARATOR = "_";
    public static final String EVENT_PREFIX = "Macro.";

    public static enum CallbackEvent {
        VIEW_BUILD(".callback.viewBuild"),
        BEFORE_VIEW_RENDER(".callback.beforeViewRender"),
        AFTER_VIEW_RENDER(".callback.afterViewRender");
        private String name;
        CallbackEvent(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }

    private String uniqueId = Long.toString(new Date().getTime());
    private String clientId;
    private MacroPluginModule metadata;
    private Map attributes = new HashMap();

    WikiPluginMacro(MacroPluginModule metadata, WikiTextMacro wikiTextMacro) {
        super(wikiTextMacro);
        this.metadata = metadata;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public MacroPluginModule getMetadata() {
        return metadata;
    }

    public void setMetadata(MacroPluginModule metadata) {
        this.metadata = metadata;
    }

    public Map getAttributes() {
        return attributes;
    }

    public void setAttributes(Map attributes) {
        this.attributes = attributes;
    }

    // Some convenience methods that generate Strings used all over the place

    // This needs to be unique _across_ different wiki texts. So the numeric position and the name is not enough,
    // the hashCode() is overriden in the superclass to be the name/position combination... so we need the uniqueId.
    // TODO: We can now actually remove the position and name, but they help us with debugging.
    public String getPageVariableName() {
        return PAGE_VARIABLE_PREFIX + getPosition() + PAGE_VARIABLE_SEPARATOR + this.uniqueId + PAGE_VARIABLE_SEPARATOR + getName();
    }

    public String getCallbackEventName(CallbackEvent event) {
        return EVENT_PREFIX+getName()+event.getName();
    }

    public String getRequestImagePath() {
        return getRequestPathPrefix() + getMetadata().getImagePath();
    }

    public String getRequestStylesheetPath() {
        return getRequestPathPrefix() + getMetadata().getStylesheetPath();
    }

    private String getRequestPathPrefix() {
        if (getClientId() == null) {
            // We need to prefix any request with the currenct web context name if this
            // is not a JSF request. That means, at this time, simple resource (img, css)
            // GET requests that use EL which in turn uses instances of this class.
            return ServletContexts.instance().getRequest().getContextPath();
        }
        return "";
    }
    
    public String getCacheRegion(String name) {
        return getMetadata().getQualifiedCacheRegionName(name);
    }

    public String getMessage(String message) {
        return Messages.instance().get(getMetadata().getPlugin().getKey() + "." + message);
    }

    /*
        Cache keys for macros are unique hashes:

        - unique in all wiki areas: the id of the current document
        - unique in a particular document: the name and position of the macro in the document
        - unique with changing macro parameters: the hashcode of any macro parameters
        - unique for a particular user access level: the current users access level
        - unique considering the hashCode() of any additional objects passed to the method

     */
    public String getCacheKey() {
        return getCacheKey(new Object[]{});
    }

    public String getCacheKey(Object o) {
        return getCacheKey(new Object[]{o});
    }

    public String getCacheKey(Object... objects) {
        WikiDocument currentDocument = (WikiDocument)Component.getInstance("currentDocument");
        Integer accessLevel = (Integer) Component.getInstance("currentAccessLevel");
        Hash hash = (Hash)Component.getInstance(Hash.class);

        log.debug("generating cache key for document: " + currentDocument + " and macro: " + this + " and access level: " + accessLevel);
        StringBuilder builder = new StringBuilder();

        if (log.isDebugEnabled()) log.debug("including id of document: " + currentDocument.getId());
        builder.append( currentDocument.getId() );

        int namePositionHash = (getName() + "_" + getPosition()).hashCode();
        if (log.isDebugEnabled()) log.debug("including name/position of this macro: " + Math.abs(namePositionHash));
        builder.append( Math.abs(namePositionHash) );

        if (log.isDebugEnabled()) log.debug("including hashCode of macro params: " + Math.abs(getParams().hashCode()));
        builder.append( Math.abs(getParams().hashCode()) );

        if (log.isDebugEnabled()) log.debug("including accessLevel: " + accessLevel);
        builder.append( accessLevel );

        // This needs to be empty-String safe (the additional objects might be some of the
        // JSF "oh let's map a non-existant request parameter to an empty string" genius behavior...
        if (objects != null && objects.length > 0) {
            for (Object o : objects) {
                if (o != null && Math.abs(o.hashCode()) != 0) {
                    log.debug("including hashCode of object: " + Math.abs(o.hashCode()));
                    builder.append( Math.abs(o.hashCode()) );
                }
            }
        }
        return hash.hash(builder.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WikiPluginMacro that = (WikiPluginMacro) o;
        return uniqueId.equals(that.uniqueId);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + uniqueId.hashCode();
        return result;
    }

    public String toString() {
        return "WikiPluginMacro UniqueId '" + this.uniqueId + "' (" + getPosition() + "): "
                + getName() + " Params: " + getParams().size();
    }

}
