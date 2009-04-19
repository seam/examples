/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.util;

import org.jboss.seam.Component;
import org.jboss.seam.security.Identity;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.preferences.Preferences;

import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Adds stuff to and for JSF that should be there but isn't. Also stuff that is exposed
 * as a Facelets function, and various other useful static methods that are called from
 * everywhere.
 *
 * @author Christian Bauer
 */
public class WikiUtil {

    // Disable caching of imags (e.g. captcha) by appending this as a random URL parameter
    public static int generateRandomNumber() {
        return (int) Math.round(1 + (Math.random()*1000000));
    }

    // Creates clean alphanumeric UpperCaseCamelCase
    public static String convertToWikiName(String realName) {
        StringBuilder wikiName = new StringBuilder();
        // Remove everything that is not alphanumeric or whitespace, then split on word boundaries
        String[] tokens = realName.replaceAll("[^\\p{Alnum}|\\s]+", "").split("\\s");
        for (String token : tokens) {
            // Append word, uppercase first letter of word
            if (token.length() > 1) {
                wikiName.append(token.substring(0,1).toUpperCase());
                wikiName.append(token.substring(1));
            } else {
                wikiName.append(token.toUpperCase());
            }
        }
        return wikiName.toString();
    }

    public static String truncateString(String string, int length, String appendString) {
        if (string.length() <= length) return string;
        return string.substring(0, length-1) + appendString;
    }

    public static String truncateStringOnWordBoundary(String string, int length) {
        if (string.length() <= length) return string;

        char [] chars = string.toCharArray();
        StringBuffer buffer = new StringBuffer();
        String result = "";
        int lastWhitespace = 0;
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ' ') lastWhitespace = i;
            buffer.append(chars[i]);

            if (i >= length) {
                result = buffer.substring(0, lastWhitespace);
                break;
            }
        }
        return result;
    }

    public static String concat(String a, String b) {
        return a + b;
    }

    public static String padInteger(Integer raw, int padding) {
        String rawInteger = raw.toString();
        StringBuilder paddedInteger = new StringBuilder( );
        for ( int padIndex = rawInteger.length() ; padIndex < padding; padIndex++ ) {
            paddedInteger.append('0');
        }
        return paddedInteger.append( rawInteger ).toString();
    }

    public static String dateAsString(Integer year, Integer month, Integer day) {
        StringBuilder dateUrl = new StringBuilder();
        if (year != null) dateUrl.append("/Year/").append(year);
        if (month != null) dateUrl.append("/Month/").append(WikiUtil.padInteger(month, 2));
        if (day != null) dateUrl.append("/Day/").append(WikiUtil.padInteger(day, 2));
        return dateUrl.toString();
    }

    public static boolean isLastItemInList(List list, Object o) {
        return list.contains(o) && !(list.indexOf(o) < list.size()-1);
    }

    // Display all roles for a particular access level
    public static Role.AccessLevel resolveAccessLevel(Integer accessLevel) {
        List<Role.AccessLevel> accessLevels = (List<Role.AccessLevel>)Component.getInstance("accessLevelsList");
        return accessLevels.get(
                accessLevels.indexOf(new Role.AccessLevel(accessLevel, null))
               );
    }

    public static boolean showEmailAddress() {
        Integer accessLevel = (Integer)Component.getInstance("currentAccessLevel");
        if (Preferences.instance().get(WikiPreferences.class).isShowEmailToLoggedInOnly()
                && Identity.instance().isLoggedIn()
                && accessLevel == Role.ADMINROLE_ACCESSLEVEL) {
            return true;
        } else if (!Preferences.instance().get(WikiPreferences.class).isShowEmailToLoggedInOnly()) {
            return true;
        }
        return false;
    }

    public static String displayFilesize(int fileSizeInBytes) {
        // TODO: Yeah, that could be done smarter..
        if (fileSizeInBytes >= 1073741824) {
            return new BigDecimal(fileSizeInBytes / 1024 / 1024 / 1024) + " GiB";
        }else if (fileSizeInBytes >= 1048576) {
            return new BigDecimal(fileSizeInBytes / 1024 / 1024) + " MiB";
        } else if (fileSizeInBytes >= 1024) {
            return new BigDecimal(fileSizeInBytes / 1024) + " KiB";
        } else {
            return new BigDecimal(fileSizeInBytes) + " Bytes";
        }
    }

    public static String encodeURL(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String decodeURL(String string) {
        try {
            return URLDecoder.decode(string, "UTF-8");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String escapeJSMessage(String message) {
        return message.replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\\\"");
    }

    public static String escapeEmailURL(String string) {
        return string.length() >= 7 && string.substring(0, 7).equals("mailto:")
                ? string.replaceAll("@", Preferences.instance().get(WikiPreferences.class).getAtSymbolReplacement())
                : string;
    }

    public static String escapeAtSymbol(String string) {
        return string.replaceAll("@", Preferences.instance().get(WikiPreferences.class).getAtSymbolReplacement());
    }

    public static String escapeHtml(String string, boolean convertNewlines, boolean convertSpaces) {
        if (string == null) return null;
        StringBuilder sb = new StringBuilder();
        String htmlEntity;
        char c;
        for (int i = 0; i < string.length(); ++i) {
            htmlEntity = null;
            c = string.charAt(i);
            switch (c) {
                case '<': htmlEntity = "&lt;"; break;
                case '>': htmlEntity = "&gt;"; break;
                case '&': htmlEntity = "&amp;"; break;
                case '"': htmlEntity = "&quot;"; break;
            }
            if (htmlEntity != null) {
                sb.append(htmlEntity);
            } else {
                sb.append(c);
            }
        }
        String result = sb.toString();
        if (convertSpaces) {
            // Converts the _beginning_ of line whitespaces into non-breaking spaces
            Matcher matcher = Pattern.compile("(\\n+)(\\s*)(.*)").matcher(result);
            StringBuffer temp = new StringBuffer();
            while(matcher.find()) {
                String group = matcher.group(2);
                StringBuilder spaces = new StringBuilder();
                for (int i = 0; i < group.length(); i++) {
                    spaces.append("&#160;");
                }
                matcher.appendReplacement(temp, "$1"+spaces.toString()+"$3");
            }
            matcher.appendTail(temp);
            result = temp.toString();
        }
        if (convertNewlines) {
            result = result.replaceAll("\n", "<br/>");
        }
        return result;
    }

    public static String removeHtml(String original) {
        if (original == null) return null;
        return original.replaceAll("\\<([a-zA-Z]|/){1}?.*?\\>","");
    }

    // TODO: Ouch...
    public static String removeMacros(String string) {
        if (string == null) return null;
        String REGEX_MACRO = Pattern.quote("[") + "<=[a-z]{1}?[a-zA-Z0-9]+?" + Pattern.quote("]");
        return string.replaceAll(REGEX_MACRO, "");

    }

    // TODO: This would be the job of a more flexible seam text parser...
    public static String disableFloats(String string) {
        return string.replaceAll("float:\\s?(right)|(left)", "float:none")
                     .replaceAll("width:\\s?[0-9]+\\s?(px)", "width:100%")
                     .replaceAll("width:\\s?[0-9]+\\s?(%)", "width:100%");
    }

    public static byte[] resizeImage(byte[] imageData, String contentType, int width) {
        ImageIcon icon = new ImageIcon(imageData);

        double ratio = (double) width / icon.getIconWidth();
        int resizedHeight = (int) (icon.getIconHeight() * ratio);

        int imageType = "image/png".equals(contentType)
                        ? BufferedImage.TYPE_INT_ARGB
                        : BufferedImage.TYPE_INT_RGB;
        BufferedImage bImg = new BufferedImage(width, resizedHeight, imageType);
        Graphics2D g2d = bImg.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(icon.getImage(), 0, 0, width, resizedHeight, null);
        g2d.dispose();

        String formatName = "";
        if ("image/png".equals(contentType))       formatName = "png";
        else if ("image/jpeg".equals(contentType) || "image/jpg".equals(contentType)) formatName = "jpeg";

        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        try {
            ImageIO.write(bImg, formatName, baos);
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Throwable unwrap(Throwable throwable) throws IllegalArgumentException {
        if (throwable == null) {
            throw new IllegalArgumentException("Cannot unwrap null throwable");
        }
        for (Throwable current = throwable; current != null; current = current.getCause()) {
            throwable = current;
        }
        return throwable;
    }

    public static int getSessionTimeoutSeconds() {
        return ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true)).getMaxInactiveInterval();
    }

    /**
     * Moves the element at <tt>oldPosition</tt> to <tt>newPosition</tt>.
     * <p>
     * Correctly handles forward and backward shifting of previous or subsequent elements.
     *
     * @param list the list that is affected
     * @param oldPosition the position of the element to move
     * @param newPosition the new position of the element
     */
    public static void shiftListElement(List list, int oldPosition, int newPosition) {
        if (oldPosition> newPosition) {
            Collections.rotate(list.subList(newPosition, oldPosition+1), +1);
        } else if (oldPosition < newPosition) {
            Collections.rotate(list.subList(oldPosition, newPosition+1), -1);
        }
    }

    // Some null-safe operations
    public static int sizeOf(Collection col) {
        return col == null ? 0 : col.size();
    }
    public static int length(String string) {
        return string == null ? 0 : string.length();
    }

    public static String repeatString(String s, Integer count) {
        StringBuilder spaces = new StringBuilder();
        for (int i = 0; i < count; i++) {
            spaces.append(s);
        }
        return spaces.toString();
    }

    public static Date toDate(Long time) {
        return new Date(time);
    }

    public static String getTimeDifferenceToCurrent(Date d) {
        return getTimeDifference(new Date(), d);
    }

    public static String getTimeDifference(Date a, Date b) {
        long time = a.getTime() > b.getTime() ? a.getTime() - b.getTime() : b.getTime() - a.getTime();
        int seconds = (int)((time/1000) % 60);
        int minutes = (int)((time/60000) % 60);
        int hours = (int)((time/3600000) % 24);
        String secondsStr = (seconds<10 ? "0" : "")+seconds;
        String minutesStr = (minutes<10 ? "0" : "")+minutes;
        String hoursStr = (hours<10 ? "0" : "")+hours;
        return hoursStr + "h:" + minutesStr + "m:" + secondsStr + "s";
    }

    public static String formatDate(Date date) {
        // TODO: Exceptional date formatting here...
        SimpleDateFormat fmt = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        return fmt.format(date);
    }

    public static Date currentDate() {
        return new Date();
    }

    public static String attachSignature(String wikiText, String sig) {
        StringBuilder builder = new StringBuilder();
        builder.append(wikiText).append("\n\n-- ").append(sig);
        return builder.toString();
    }

    public static boolean isRegularUser(User user) {
        return user != null &&
               !(user.getUsername().equals(User.ADMIN_USERNAME) || user.getUsername().equals(User.GUEST_USERNAME));
    }

    public static boolean isGuestOrAdminUsername(String username) {
        return User.ADMIN_USERNAME.equals(username) || User.GUEST_USERNAME.equals(username);
    }

    /**
     * Calculate an RFC 2822 compliant message identifier from a numeric + string identifier. Given
     * the same numeric and string identifier, the same message id will be generated.
     */
    public static String calculateMessageId(Long id, String s) {

        WikiPreferences prefs = Preferences.instance().get(WikiPreferences.class);
        Hash hash = (Hash)Component.getInstance(Hash.class);
        String domain;
        try {
            URI uri = new URI(prefs.getBaseUrl());
            domain = uri.getHost();
        } catch (Exception ex) {
            throw new RuntimeException("Could not parse preferences value baseUrl into a host name", ex);
        }
        StringBuilder msgId = new StringBuilder();
        msgId.append("<").append(hash.hash(id+s)).append("@").append(domain).append(">");
        return msgId.toString();
    }

    public static String convertUnderscoreToCamelCase(String s) {
        StringBuilder sb = new StringBuilder();
        boolean uppercaseNextChar = false;
        for (char c : s.toCharArray()) {
            if (c == '_') {
                uppercaseNextChar = true;
            } else {
                if (uppercaseNextChar) {
                    sb.append(Character.toString(c).toUpperCase());
                    uppercaseNextChar = false;
                } else {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

}
