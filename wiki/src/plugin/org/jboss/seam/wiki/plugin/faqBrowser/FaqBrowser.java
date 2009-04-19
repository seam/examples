/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.faqBrowser;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiNode;
import org.jboss.seam.wiki.core.nestedset.query.NestedSetNodeWrapper;

import java.util.List;
import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("faqBrowser")
@Scope(ScopeType.PAGE)
public class FaqBrowser implements Serializable {

    @Logger
    Log log;

    @In(create = true)
    FaqBrowserDAO faqBrowserDAO;

    @In
    WikiNodeDAO wikiNodeDAO;

    @In
    WikiDirectory currentDirectory;

    @In(required = false)
    String requestedCategory;

    NestedSetNodeWrapper<WikiDirectory> tree;
    NestedSetNodeWrapper<WikiDirectory> selectedDir;
    boolean directorySelected = false;

    public void loadTree() {
        log.debug("loading faq root, starting search for parent default file with macro in directory: " + currentDirectory);
        WikiDirectory faqRoot = faqBrowserDAO.findFaqRootDir(currentDirectory);
        if (faqRoot != null) {
            log.debug("found faq root: " + faqRoot);
            tree = wikiNodeDAO.findWikiDirectoryTree(faqRoot, 99l, 1l, false);
        } else {
            log.warn("did not find faq root, started search in: " + currentDirectory);
        }
        if (tree == null) {
            log.warn("faq directory tree has not been loaded");
        }
    }

    @Create
    public void setDefaultDir() {
        if (requestedCategory != null) {
            log.debug("trying to resolve requested category: " + requestedCategory);
            WikiDirectory dir = wikiNodeDAO.findWikiDirectoryInArea(currentDirectory.getAreaNumber(), requestedCategory);
            if (dir != null) {
                log.debug("found requested category, setting selected directory: " + dir);
                selectedDir = new NestedSetNodeWrapper<WikiDirectory>(dir);
                showQuestions();
            }
        }
        if (selectedDir == null) {
            log.debug("setting selected directory to current directory");
            selectedDir =
                new NestedSetNodeWrapper<WikiDirectory>(
                    wikiNodeDAO.findWikiDirectory(currentDirectory.getId())
                );
        }
    }

    public NestedSetNodeWrapper<WikiDirectory> getTree() {
        log.debug("getting faq tree");
        if (tree == null) loadTree();
        return tree;
    }

    public NestedSetNodeWrapper<WikiDirectory> getSelectedDir() {
        log.debug("getting selected directory : " + selectedDir);
        return selectedDir;
    }

    public void setSelectedDir(NestedSetNodeWrapper<WikiDirectory> selectedDir) {
        log.debug("setting selected directory: " + selectedDir);
        this.selectedDir = selectedDir;
    }

    public boolean isDirectorySelected() {
        return directorySelected;
    }

    @Observer("FaqBrowser.questionListRefresh")
    public void showQuestions() {
        log.debug("showing questions of currently selected directory: " + selectedDir.getWrappedNode());
        directorySelected = true;
        questions = wikiNodeDAO.findWikiDocuments(selectedDir.getWrappedNode(), WikiNode.SortableProperty.createdOn, true);
    }

    public void hideQuestions() {
        log.debug("hiding questions");
        directorySelected = false;
        this.questions = null;
    }

    List<WikiDocument> questions;

    public List<WikiDocument> getQuestions() {
        log.debug("retrieving questions: " + questions);
        return questions;
    }


}
