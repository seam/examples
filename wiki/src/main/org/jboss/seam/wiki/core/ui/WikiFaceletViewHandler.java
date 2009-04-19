package org.jboss.seam.wiki.core.ui;

import com.sun.facelets.FaceletViewHandler;
import com.sun.facelets.util.DevTools;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Christian Bauer
 */
public class WikiFaceletViewHandler extends FaceletViewHandler {

    protected boolean developmentMode; // Private in superclass, hurray!

    public WikiFaceletViewHandler(ViewHandler viewHandler) {
        super(viewHandler);
    }

    @Override
    protected void initialize(FacesContext context) {
        super.initialize(context);

        ExternalContext external = context.getExternalContext();
        String param = external.getInitParameter(PARAM_DEVELOPMENT);
        this.developmentMode = "true".equals(param);
    }

    @Override
    protected void handleRenderException(FacesContext context, Exception e)
            throws IOException, ELException, FacesException {

        Object resp = context.getExternalContext().getResponse();

/* This is what we don't like: log and rethrow! Makes Facelets nasty in production use...
        // always log
        if (log.isLoggable(Level.SEVERE)) {
            UIViewRoot root = context.getViewRoot();
            StringBuffer sb = new StringBuffer(64);
            sb.append("Error Rendering View");
            if (root != null) {
                sb.append('[');
                sb.append(root.getViewId());
                sb.append(']');
            }
            log.log(Level.SEVERE, sb.toString(), e);
        }
*/
        // handle dev response
        if (this.developmentMode && !context.getResponseComplete()
                && resp instanceof HttpServletResponse) {
            HttpServletResponse httpResp = (HttpServletResponse) resp;
            httpResp.reset();
            httpResp.setContentType("text/html; charset=UTF-8");
            Writer w = httpResp.getWriter();
            DevTools.debugHtml(w, context, e);
            w.flush();
            context.responseComplete();
        } else if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else if (e instanceof IOException) {
            throw (IOException) e;
        } else {
            throw new FacesException(e.getMessage(), e);
        }
    }
}
