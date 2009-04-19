/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.wikitext.engine;

import org.jboss.seam.wiki.core.model.WikiFile;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * The heart of the wiki, converts and resolves human-readable link tags from and to permanent
 * links that can be stored (or read) persistently. Also resolves link texts (from stored
 * link tags) to <tt>WikiLink</tt> objects, for rendering.
 * <p>
 * Use the supplied regular expressions to implement the methods, or parse the wiki text
 * completely by hand and convert/resolve links.
 *
 * TODO: With a new multi-stage SeamTextParser we could remove most of this regex stuff
 *
 * @author Christian Bauer
 */
public interface WikiLinkResolver {

    /**
     * Matches known protocols, e.g. [=>http://foo.bar], which can be ignored and "resolved" as-is
     */
    public static final String REGEX_KNOWN_PROTOCOL = "^(http://)|(https://)|(ftp://)|(mailto:).+$";

    /**
     * Matches customized protocols, e.g. [=>seamjira://123], which should be resolved and rendered
     */
    public static final String REGEX_CUSTOM_PROTOCOL = "^([a-zA-Z]+)://(.+)$";

    /**
     * Matches WikiNode.getName() constraint.
     */
    public static final String REGEX_NODE_NAME = "[a-zA-Z0-9]+[^/#\\|\\]\\[]*";

    /**
     * Matches URL fragments, punctuation is limited, so not all fragments are reachable.
     */
    public static final String REGEX_FRAGMENT = "#[\\w\\s\\.\\,\\;\\-\\?\\!\\(\\)\\&\\/]+";

    /**
     * Matches "Node Name#Fragment123" with two groups.
     */
    public static final String REGEX_NODE_NAME_FRAGMENT = "("+REGEX_NODE_NAME+")(" + REGEX_FRAGMENT + ")?";

    /**
     * Prepended to primary identifiers when links are stored, e.g. [This is a stored link=>wiki://5]
     */
    public static final String REGEX_WIKI_PROTOCOL = "wiki://([0-9]+)("+REGEX_FRAGMENT+")?";

    /**
     * Match [GROUP1=>GROUP1], used to replace links from user input with wiki:// URLs - used
     * in <tt>convertToWikiProtocol()</tt>.
     */
    public static final String REGEX_WIKILINK_FORWARD =
            Pattern.quote("[") + "([^" + Pattern.quote("]") + "|" + Pattern.quote("[") + "]*)" +
            "=>([^" + Pattern.quote("]") + Pattern.quote("[") + "]+)" + Pattern.quote("]");

    /**
     * Match [GROUP1=>wiki://GROUP2], used to replace wiki:// URLs with page names
     */
    public static final String REGEX_WIKILINK_REVERSE =
            Pattern.quote("[") + "([^" + Pattern.quote("]") + "|" + Pattern.quote("[") + "]*)" +
            "=>" + REGEX_WIKI_PROTOCOL + Pattern.quote("]");

    /**
     * Match "Foo Bar|Baz Brrr#Fragment" as three groups
     */
    public static final String REGEX_WIKILINK_CROSSAREA = "^(" + REGEX_NODE_NAME +")"+ Pattern.quote("|") + REGEX_NODE_NAME_FRAGMENT + "$";

    /**
     * Replaces clear text links such as <tt>[Link description=>Target Name]</tt> in <tt>wikiText</tt> with
     * <tt>[Link description=>wiki://id]</tt> strings, usually resolves the target name as a unique wiki name
     * in some data store.
     * The <tt>currentAreaNumber</tt> of the current document is supplied and can be used as the namespace for scoped resolving.
     * <p>
     * This method should be called whenever a wiki document is stored, we want to store the permanent
     * identifiers of a target node. That way, the target node can be renamed and the document that links
     * to that target node still contains the valid link.
     * </p><p>
     * Either parse the <tt>wikiText</tt> by hand to find and replace links, or use the
     * <tt>REGEX_WIKILINK_FORWARD</tt> pattern which matches <tt>[GROUP1=>GROUP2]</tt>.
     * Convert the target name (<tt>GROUP2</tt>) to a unique wiki name, and then to some primary
     * identifier which you can lookup again in the future in a reliable fashion. <tt>GROUP1</tt> is
     * the optional link description entered by the user, you need to keep this string and only replace
     * <tt>GROUP2</tt> with a permanent identifier (prefixed with the <tt>wiki://</tt> protocol).
     * </p><p>
     * Note that cross-namespace linking should be supported, so in addition to <tt>[=>Target Name]</tt>,
     * links can be entered by the user as <tt>[=>Target Area|Target Name]</tt>. To resolve these link
     * texts, use <tt>REGEX_WIKILINK_CROSSAREA</tt> on the original <tt>GROUP2</tt>, which produces
     * two groups. Ignore the given <tt>currentAreaNumber</tt> parameter and resolve in the target namespace entered by
     * the user on the link tag.
     * </p><p>
     * Example pseudo code:
     * </p><p>
     * <pre>
     * if (targetName = wikiText.match(REGEX_WIKI_LINK_FORWARD)) {
     *
     *     if (targetNamespace, newTargetName = targetName.match(REGEX_WIKILINK_CROSSAREA) {
     *
     *         wikiText.replace( resolveNodeId(targetNamespace, newTargetName) );
     *
     *     } else {
     *         wikiText.replace( resolveNodeId(givenNamespace, targetName) );
     *     }
     * }
     * </pre>
     *
     * @param linkTargets       This collection will be filled with <tt>WikiFile</tt> instances which are the link targets in the wiki text
     * @param currentAreaNumber The currennt area useable as the namespace for scoped resolving
     * @param wikiText Text with wiki markup containing [=>Target Name] links
     * @return The <tt>wikiText</tt> with all <tt>[=>Target Name]<tt> links replaced with <tt>[=>wiki://id]</tt>
     */
    public String convertToWikiProtocol(Set<WikiFile> linkTargets, Long currentAreaNumber, String wikiText);

    /**
     * Replace stored text links such as <tt>[Link description=>wiki://id]</tt> with clear text target names, so
     * users can edit the link again in clear text.
     * </p><p>
     * Either parse by hand or use the <tt>REGEX_WIKILINK_REVERSE</tt> pattern, which matches
     * <tt>[GROUP1=>(wiki://GROUP2)]. Replace with <tt>[GROUP1=>Target Name]</tt> or, if the target is not in
     * the same namespace as the given <tt>area</tt> parameter, append the area:
     * <tt>[GROUP1=>Target Area|Target Name]</tt>.
     *
     * @param currentAreaNumber The current area useable as the namespace for scoped resolving
     * @param wikiText Text with wiki markup containing [=>wiki://id] links
     * @return The <tt>wikiText</tt> with all <tt>[=>wiki://id]<tt> links replaced with <tt>[=>Target Name]</tt>
     */
    public String convertFromWikiProtocol(Long currentAreaNumber, String wikiText);

    /**
     * Resolve the given <tt>linkText</tt> to an instance of <tt>WikiLink</tt> and put it in the <tt>links</tt> map.
     * <p>
     * The <tt>WikiLink</tt> objects are used during rendering, the rules are as follows:
     * <ul>
     * <li>If the <tt>linkText</tt> matches <tt>REGEX_KNOWN_PROTOCOL</tt>, don't resolve but create
     * a <tt>WikiLink</tt> instance that contains <tt>url</tt>, <tt>description</tt> (same as <tt>url</tt>),
     * <tt>broken=false</tt>, <tt>external=true</tt>. The <tt>url</tt> is the actual <tt>linkText</tt>, as-is.
     * </li>
     * <li>If the <tt>linkText</tt> matches <tt>REGEX_WIKI_PROTOCOL</tt>, resolve it and create
     * a <tt>WikiLink</tt> instance that contains the resolved <tt>Node</tt> instance, the node name
     * as <tt>description</tt>, no <tt>url</tt>, and <tt>external=false</tt>. If the <tt>linkText</tt>
     * can't be resolved to a <tt>Node</tt>, set <tt>broken=true</tt>, a null <tt>node</tt>, and whatever
     * <tt>url</tt> and <tt>description</tt> you want to render for a broken link.
     * </li>
     * <li>Otherwise, the <tt>linkText</tt> represents a clear text link such as <tt>Target Name</tt> or
     * <tt>Target Area|TargetName</tt>, which you can resolve if you want and return a
     * <tt>WikiLink</tt> instance as in the previous rule. If it can't be resolved, return a broken link
     * indicator as described in the previous rule. If it has been resolved, you may indicate that the
     * original document that contains this <tt>linkText</tt> should be updated in the datastore (usually
     * by passing its wiki text content through <tt>convertToWikiProtocol</tt>) - set <tt>requiresUpdating=true</tt>
     * on the <tt>WikiLink</tt> instance. It's the job of the client of this resolver to handle this flag
     * (or to ignore it).
     *</li>
     * </ul>
     * 
     * @param currentAreaNumber The current area useable as the namespace for scoped resolving
     * @param links A map of all resolved <tt>WikiLink</tt> objects, keyed by <tt>linkText</tt>
     * @param linkText A stored link text, such as "wiki://123" or "http://foo.bar" or "Target Area|Target Name]"
     */
    public void resolveLinkText(Long currentAreaNumber, Map<String, WikiLink> links, String linkText);

    public Long resolveWikiDirectoryId(Long currentAreaNumber, String linkText);

    public Long resolveWikiDocumentId(Long currentAreaNumber, String linktext);

}
