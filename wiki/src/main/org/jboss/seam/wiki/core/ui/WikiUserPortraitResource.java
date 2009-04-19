/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import static org.jboss.seam.ScopeType.APPLICATION;
import org.jboss.seam.web.AbstractResource;
import org.jboss.seam.log.Logging;
import org.jboss.seam.log.Log;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Serves user portraits from the database, so we don't have to use s:graphicImage anymore.
 *
 * This helps caching on the browser, as image URIs will be stable and not random keys into a current HttpSession.
 *
 * @author Christian Bauer
 */
@Scope(APPLICATION)
@Name("wikiUserPortraitThemeResource")
@BypassInterceptors
public class WikiUserPortraitResource extends AbstractResource {

    // Resources URIs end with /<userId>/<l|s>
    public static Pattern RESOURCE_PATTERN = Pattern.compile("^/([0-9]+)/([ls]{1})$");

    public static final String REGISTER_SEAM_RESOURCE = "/wikiUserPortrait";

    private Log log = Logging.getLog(WikiUserPortraitResource.class);

    @Override
    public String getResourcePath() {
        return REGISTER_SEAM_RESOURCE;
    }

    @Override
    public void getResource(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        // Wrap this, we need an ApplicationContext
        new ContextualHttpServletRequest(request) {
            @Override
            public void process() throws IOException {
                doWork(request, response);
            }
        }.run();

    }

    public void doWork(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String pathInfo = request.getPathInfo().substring(getResourcePath().length());

        String userId = null;
        String imageSize = null;
        Matcher matcher = RESOURCE_PATTERN.matcher(pathInfo);
        if (matcher.find()) {
            userId = matcher.group(1);
            imageSize = matcher.group(2);
            log.debug("request for user id: " + userId + ", image size: " + imageSize);
        }

        if (userId == null || imageSize == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request path, use: /wikiUserPortrait/[0-9]+/(l|s)");
            return;
        }

        UserDAO userDAO = (UserDAO) Component.getInstance(UserDAO.class);
        User user = userDAO.findUser(Long.valueOf(userId));
        if (user == null || user.getProfile().getImageContentType() == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User id not found or no portrait for user");
            return;
        }

        response.addHeader("Cache-Control", "max-age=600"); // 10 minutes freshness in browser cache

        byte[] image = imageSize.equals("l") ? user.getProfile().getImage() : user.getProfile().getSmallImage();
        response.setContentType(user.getProfile().getImageContentType());
        response.setContentLength(image.length);
        response.getOutputStream().write(image);
        response.getOutputStream().flush();
    }

}
