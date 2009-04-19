/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.basic;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.nestedset.query.NestedSetNodeWrapper;
import org.jboss.seam.wiki.core.plugin.WikiPluginMacro;
import org.jboss.seam.wiki.preferences.Preferences;

import java.io.Serializable;

/**
 * Menu tree, base is the current directory.
 *
 * @author Christian Bauer
 */
@Name("dirMenu")
@Scope(ScopeType.PAGE)
public class DirMenu implements Serializable {

    public static final String MACRO_ATTR_ROOT = "dirMenuRoot";

    @In
    WikiDirectory currentDirectory;

    public NestedSetNodeWrapper<WikiDirectory> getRoot(WikiPluginMacro macro) {
        // We cache the result in the macro, so that when the getter is called over and over during rendering, we have it
        if (macro.getAttributes().get(MACRO_ATTR_ROOT) == null) {
            NestedSetNodeWrapper<WikiDirectory> root;
            DirMenuPreferences prefs  = Preferences.instance().get(DirMenuPreferences.class, macro);
            if (prefs.getOnlyMenuItems() != null && prefs.getOnlyMenuItems()) {
                root = WikiNodeDAO.instance().findMenuItemTree(currentDirectory, 3l, 3l, false);
            } else {
                root = WikiNodeDAO.instance().findWikiDirectoryTree(currentDirectory, 3l, 3l, false);
            }
            macro.getAttributes().put(MACRO_ATTR_ROOT, root);
        }
        return (NestedSetNodeWrapper<WikiDirectory>)macro.getAttributes().get(MACRO_ATTR_ROOT);
    }

}
