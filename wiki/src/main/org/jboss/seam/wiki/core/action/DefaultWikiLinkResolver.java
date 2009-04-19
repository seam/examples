package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.wikitext.engine.WikiLinkResolver;
import org.jboss.seam.wiki.core.wikitext.engine.WikiLink;
import org.jboss.seam.wiki.util.WikiUtil;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A default implementation of <tt>WikiLinkResolver</tt>.
 *
 * @author Christian Bauer
 */
@Name("wikiLinkResolver")
@AutoCreate
public class DefaultWikiLinkResolver implements WikiLinkResolver {

    @Logger static Log log;

    // Render these strings whenever [=>wiki://123] needs to be resolved but can't
    public static final String BROKENLINK_URL = "FileNotFound";
    public static final String BROKENLINK_DESCRIPTION = "?BROKEN LINK?";

    @In
    private WikiNodeDAO wikiNodeDAO;

    @In
    Map<String, LinkProtocol> linkProtocolMap;

    public String convertToWikiProtocol(Set<WikiFile> linkTargets, Long currentAreaNumber, String wikiText) {
        if (wikiText == null) return null;

        log.debug("converting wiki text links to wiki protocol for storage, current area: " + currentAreaNumber);

        StringBuffer replacedWikiText = new StringBuffer(wikiText.length());
        Matcher matcher = Pattern.compile(REGEX_WIKILINK_FORWARD).matcher(wikiText);

        // Replace with [Link Text=>wiki://<node id>] or leave as is if not found
        while (matcher.find()) {
            String linkText = matcher.group(2);

            if (linkText.matches(REGEX_KNOWN_PROTOCOL) || linkText.matches(REGEX_CUSTOM_PROTOCOL)) continue;

            log.debug("converting to wiki protocol: " + linkText);

            WikiFile file = null;
            String fragment = null;
            Matcher linkTextMatcher = getCrossAreaMatcher(linkText);
            if (linkTextMatcher != null) {
                log.debug("link to different area: " + linkTextMatcher.group(1));
                file = resolve(currentAreaNumber, linkTextMatcher.group(1), linkTextMatcher.group(2));
                fragment = linkTextMatcher.group(3);
            } else {
                log.debug("link to current area");
                linkTextMatcher = getAreaMatcher(linkText);
                if (linkTextMatcher != null) {
                    file = resolve(currentAreaNumber, null, linkTextMatcher.group(1));
                    fragment = linkTextMatcher.group(2);
                }
            }

            log.debug("resolved file: "  + file);
            log.debug("resolved fragment: " + fragment);

            if (file != null) {
                if (fragment == null) fragment = "";
                log.debug("updating wiki text with wiki protocol link: " + "wiki://" + file.getId() + ""+fragment);
                matcher.appendReplacement(replacedWikiText, "[$1=>wiki://" + file.getId() + ""+fragment+"]");
                linkTargets.add(file);
            }
        }
        matcher.appendTail(replacedWikiText);
        log.debug("completed converting wiki text links to wiki protocol, ready for storing");
        return replacedWikiText.toString();
    }

    public String convertFromWikiProtocol(Long currentAreaNumber, String wikiText) {
        if (wikiText == null) return null;
        
        log.debug("converting wiki protocol to wiki text, current area: " + currentAreaNumber);

        StringBuffer replacedWikiText = new StringBuffer(wikiText.length());
        Matcher matcher = Pattern.compile(REGEX_WIKILINK_REVERSE).matcher(wikiText);

        // Replace with [Link Text=>Page Name] or replace with BROKENLINK "page name"
        while (matcher.find()) {

            String fileId = matcher.group(2);
            String fragment = matcher.group(3);
            log.debug("found file id: " + fileId);
            log.debug("found fragment: " + fragment);
            if (fragment == null) fragment = "";

            // Find the node by PK
            WikiFile file = wikiNodeDAO.findWikiFile(Long.valueOf(fileId));

            // Node is in current area, just use its name
            if (file != null && file.getAreaNumber().equals(currentAreaNumber)) {
                matcher.appendReplacement(replacedWikiText, "[$1=>" + file.getName() + fragment +"]");

            // Node is in different area, prepend the area name
            } else if (file != null && !file.getAreaNumber().equals(currentAreaNumber)) {
                WikiDirectory area = wikiNodeDAO.findArea(file.getAreaNumber());
                matcher.appendReplacement(replacedWikiText, "[$1=>" + area.getName() + "|" + file.getName() + fragment +"]");

            // Couldn't find it anymore, its a broken link
            } else {
                matcher.appendReplacement(replacedWikiText, "[$1=>" + BROKENLINK_DESCRIPTION + "]");
            }
        }
        matcher.appendTail(replacedWikiText);
        log.debug("completed converting wiki protocol to wiki text");
        return replacedWikiText.toString();
    }

    public void resolveLinkText(Long currentAreaNumber, Map<String, WikiLink> links, String linkText) {

        // Don't resolve twice
        if (links.containsKey(linkText)) return;

        log.debug("trying to resolve link text: " + linkText);

        Matcher wikiProtocolMatcher = Pattern.compile(REGEX_WIKI_PROTOCOL).matcher(linkText.trim());
        Matcher knownProtocolMatcher = Pattern.compile(REGEX_KNOWN_PROTOCOL).matcher(linkText.trim());
        Matcher customProtocolMatcher = Pattern.compile(REGEX_CUSTOM_PROTOCOL).matcher(linkText.trim());

        WikiLink wikiLink;

        // Check if its a common protocol
        if (knownProtocolMatcher.find()) {
            wikiLink = new WikiLink(false, true);
            wikiLink.setUrl(linkText);
            wikiLink.setDescription(linkText);
            log.debug("link resolved to known protocol: " + linkText);

        // Check if it is a wiki protocol
        } else if (wikiProtocolMatcher.find()) {

            // Find the node by PK
            WikiFile file = wikiNodeDAO.findWikiFile(Long.valueOf(wikiProtocolMatcher.group(1)));
            String fragment = wikiProtocolMatcher.group(2);
            if (file != null) {
                wikiLink = new WikiLink(false, false);
                wikiLink.setFile(file);
                wikiLink.setFragment(fragment);
                wikiLink.setDescription(file.getName());
                log.debug("link text resolved to existing node: " + file + " and fragment: " + fragment);
            } else {
                // Can't do anything, [=>wiki://123] no longer exists
                wikiLink = new WikiLink(true, false);
                wikiLink.setUrl(BROKENLINK_URL);
                wikiLink.setDescription(BROKENLINK_DESCRIPTION);
                log.debug("link tet could not be resolved: " + linkText);
            }

        // Check if it is a custom protocol
        } else if (customProtocolMatcher.find()) {

            if (linkProtocolMap.containsKey(customProtocolMatcher.group(1))) {
                LinkProtocol protocol = linkProtocolMap.get(customProtocolMatcher.group(1));
                wikiLink = new WikiLink(false, true);
                wikiLink.setUrl(protocol.getRealLink(customProtocolMatcher.group(2)));
                wikiLink.setDescription(protocol.getPrefix() + "://" + customProtocolMatcher.group(2));
                log.debug("link text resolved to custom protocol: " + linkText);
            } else {
                wikiLink = new WikiLink(true, false);
                wikiLink.setUrl(BROKENLINK_URL);
                wikiLink.setDescription(BROKENLINK_DESCRIPTION);
                log.debug("link text resolved to non-existant custom protocol: " + linkText);
            }

        // It must be a stored clear text link, such as [=>Target Name] or [=>Area Name|Target Name]
        // (This can happen if the string [foo=>bar] or [foo=>bar|baz] was stored in the database because the
        //  targets didn't exist at the time of saving)
        } else {

            // Try a WikiWord search in the current or named area
            WikiFile file = null;
            String fragment = null;
            Matcher linkTextMatcher = getCrossAreaMatcher(linkText);
            if (linkTextMatcher != null) {
                file = resolve(currentAreaNumber, linkTextMatcher.group(1), linkTextMatcher.group(2));
                fragment = linkTextMatcher.group(3);
            } else {
                linkTextMatcher = getAreaMatcher(linkText);
                if (linkTextMatcher != null) {
                    file = resolve(currentAreaNumber, null, linkTextMatcher.group(1));
                    fragment = linkTextMatcher.group(2);
                }
            }

            if (file!=null) {
                wikiLink = new WikiLink(false, false);
                wikiLink.setFile(file);
                wikiLink.setFragment(fragment);
                wikiLink.setDescription(file.getName());
                // Indicate that caller should update the wiki text that contains this link
                wikiLink.setRequiresUpdating(true);
                log.debug("link text resolved (needs updating to wiki protocol): " + file + " and fragment: " + fragment);

            } else {
                /* TODO: Not sure we should actually implement this..., one of these things that the wiki "designers" got wrong
                // OK, so it's not any recognized URL and we can't find a node with that wikiname
                // Let's assume its a page name and render /Area/WikiLink (but encoded, so it gets transported fully)
                // into the edit page when the user clicks on the link to create the document
                try {
                    String encodedPagename = currentDirectory.getWikiname() + "/" + URLEncoder.encode(linkText, "UTF-8");
                    wikiLink = new WikiLink(null, true, encodedPagename, linkText);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e); // Java is so great...
                }
                */
                wikiLink = new WikiLink(true, false);
                wikiLink.setUrl(BROKENLINK_URL);
                wikiLink.setDescription(BROKENLINK_DESCRIPTION);
                log.debug("could not resolve link: " + linkText);
            }
        }
        links.put(linkText, wikiLink);
    }

    public Long resolveWikiDirectoryId(Long currentAreaNumber, String linkText) {
        WikiFile f = resolveWikiFile(currentAreaNumber, linkText);
        return f != null ? f.getParent().getId() : null;
    }

    public Long resolveWikiDocumentId(Long currentAreaNumber, String linkText) {
        WikiFile f = resolveWikiFile(currentAreaNumber, linkText);
        return f != null ? f.getId() : null;
    }

    private WikiFile resolveWikiFile(Long currentAreaNumber, String linkText) {
        if (linkText == null || linkText.length() == 0) return null;
        Map<String, WikiLink> resolvedLinks = new HashMap<String, WikiLink>();
        resolveLinkText(currentAreaNumber, resolvedLinks, linkText);
        WikiLink resolvedLink = resolvedLinks.get(linkText);
        if (resolvedLink.isBroken() || resolvedLink.getFile().getId() == null) {
            return null;
        } else {
            return resolvedLink.getFile();
        }
    }

    private Matcher getCrossAreaMatcher(String linkText) {
        Matcher matcher = Pattern.compile(REGEX_WIKILINK_CROSSAREA).matcher(linkText);
        return matcher.find() ? matcher : null;
    }

    private Matcher getAreaMatcher(String linkText) {
        Matcher matcher = Pattern.compile(REGEX_NODE_NAME_FRAGMENT).matcher(linkText);
        return matcher.find() ? matcher : null;
    }

    private WikiFile resolve(Long currentAreaNumber, String areaName, String nodeName) {
        log.debug("trying to resolve, current area " + currentAreaNumber + ", search in area '" + areaName + "' for node name: " + nodeName);
        if (areaName != null) {
            WikiNode crossLinkArea = wikiNodeDAO.findArea(WikiUtil.convertToWikiName(areaName));
            if (crossLinkArea != null)
                return wikiNodeDAO.findWikiFileInArea(crossLinkArea.getAreaNumber(), WikiUtil.convertToWikiName(nodeName));
        }
        return wikiNodeDAO.findWikiFileInArea(currentAreaNumber, WikiUtil.convertToWikiName(nodeName));
    }

}
