/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.preferences;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.wiki.core.action.DocumentHome;
import org.jboss.seam.wiki.core.wikitext.engine.WikiTextParser;
import org.jboss.seam.wiki.core.model.WikiTextMacro;
import org.jboss.seam.wiki.core.plugin.WikiPluginMacro;
import org.jboss.seam.wiki.core.plugin.PluginRegistry;
import org.jboss.seam.wiki.core.wikitext.renderer.NullWikiTextRenderer;
import org.jboss.seam.wiki.plugin.basic.LastModifiedDocumentsPreferences;
import org.jboss.seam.wiki.preferences.Preferences;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

/**
 * @author Christian Bauer
 */
public class InstancePreferencesTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void readInstancePreferences() throws Exception {

        new NonFacesRequest("/docDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("documentId", "6");
            }

            protected void renderResponse() throws Exception {

                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                assert docHome.getInstance().getId().equals(6l); // Init!

                WikiTextParser parser = new WikiTextParser(docHome.getInstance().getContent(), true, false);
                parser.setRenderer(new NullWikiTextRenderer() {
                    @Override
                    public String renderMacro(WikiTextMacro macro) {

                        if (macro.getName().equals("lastModifiedDocuments")) {
                            WikiPluginMacro pluginMacro = PluginRegistry.instance().createWikiPluginMacro(macro);
                            LastModifiedDocumentsPreferences lmdPrefs =
                                    Preferences.instance().get(LastModifiedDocumentsPreferences.class, pluginMacro);
                            assert lmdPrefs.getDocumentTitleLength().equals(60l);
                        }

                        return null;
                    }
                }).parse();
            }

        }.run();
    }

    @Test
    public void updateInstancePreferences() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("documentId", "6");
                setParameter("parentDirectoryId", "3");
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                assert Conversation.instance().isLongRunning();

                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                assert docHome.getInstance().getId().equals(6l); // Init!

                docHome.getTextEditor().setValue("[<=lastModifiedDocuments[documentTitleLength=66]]");

                assert invokeMethod("#{documentHome.update}").equals("updated");
            }

        }.run();


        new NonFacesRequest("/docDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("documentId", "6");
            }

            protected void renderResponse() throws Exception {

                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                assert docHome.getInstance().getId().equals(6l); // Init!

                WikiTextParser parser = new WikiTextParser(docHome.getInstance().getContent(), true, false);
                parser.setRenderer(new NullWikiTextRenderer() {
                    @Override
                    public String renderMacro(WikiTextMacro macro) {

                        if (macro.getName().equals("lastModifiedDocuments")) {
                            WikiPluginMacro pluginMacro = PluginRegistry.instance().createWikiPluginMacro(macro);
                            LastModifiedDocumentsPreferences lmdPrefs =
                                    Preferences.instance().get(LastModifiedDocumentsPreferences.class, pluginMacro);
                            assert lmdPrefs.getDocumentTitleLength().equals(66l);
                        }

                        return null;
                    }
                }).parse();
            }

        }.run();
    }

}