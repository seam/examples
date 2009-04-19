/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.ui.component.UICache;
import org.jboss.seam.ui.util.cdk.RendererBase;
import org.jboss.seam.wiki.core.cache.PageFragmentCache;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Implementation of <tt>&lt;s:cache&gt;</tt> renderer based on EHCache.
 *
 * @author Christian Bauer
 */
public class WikiPageFragmentCacheRenderer extends RendererBase {

    private static final LogProvider log = Logging.getLogProvider(UICache.class);

    @Override
    protected Class getComponentClass() {
        return UICache.class;
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    protected void doEncodeChildren(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException {
        UICache cache = (UICache) component;
        if (cache.isEnabled()) {
            String region = cache.getRegion();
            if (region == null) {
                throw new RuntimeException("required region attribute missing on <s:cache>");
            }
            String key = cache.getKey();
            if (key == null) {
                throw new RuntimeException("required key attribute missing on <s:cache>");
            }
            log.debug("attempting to obtain from cache region '" + region +"' using key: " + key);
            String cachedContent = PageFragmentCache.instance().get(region, key);
            if (cachedContent == null) {
                log.debug("rendering from scratch: " + key);
                StringWriter stringWriter = new StringWriter();
                ResponseWriter cachingResponseWriter = writer.cloneWithWriter(stringWriter);
                context.setResponseWriter(cachingResponseWriter);
                renderChildren(context, component);
                context.setResponseWriter(writer);
                String output = stringWriter.getBuffer().toString();
                writer.write(output);
                log.debug("caching rendered content in region '" + region +"' using key: " + key);
                PageFragmentCache.instance().put(region, key, output);
            } else {
                log.debug("rendering from cache: " + key);
                writer.write(cachedContent);
            }
        } else {
            log.debug("cached rendering is disabled for: " + cache.getKey());
            renderChildren(context, component);
        }
        log.debug("rendering (including all children) complete");
    }

}
