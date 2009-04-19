/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.preferences;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.action.PreferenceEditor;
import org.jboss.seam.wiki.preferences.Preferences;
import org.jboss.seam.wiki.preferences.PreferenceValue;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceRegistry;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author Christian Bauer
 */
public class SystemPreferencesTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void readSystemPreferences() throws Exception {
        new NonFacesRequest("/docDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("documentId", "6");
            }

            protected void renderResponse() throws Exception {
                WikiPreferences wikiPrefs = Preferences.instance().get(WikiPreferences.class);
                assert wikiPrefs.getShowDocumentCreatorHistory();
                assert wikiPrefs.getShowTags();
                assert wikiPrefs.getDefaultDocumentId().equals(6l);
                assert wikiPrefs.getMemberArea().equals("Members");
            }

        }.run();
    }
    
    @Test
    public void updateSystemPreferences() throws Exception {

        loginAdmin();

        final String conversationId = new NonFacesRequest("/adminHome_d.xhtml") {}.run();

        new FacesRequest("/adminHome_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {

                PreferenceRegistry registry = (PreferenceRegistry)getInstance(PreferenceRegistry.class);
                PreferenceEntity wikiEntity = registry.getPreferenceEntitiesByName().get("Wiki");

                invokeMethod("#{adminHome.initPreferencesEditor}");

                PreferenceEditor prefEditor = (PreferenceEditor)getInstance(PreferenceEditor.class);
                prefEditor.selectPreferenceEntity(wikiEntity);
            }

        }.run();

        new FacesRequest("/adminHome_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                PreferenceEditor prefEditor = (PreferenceEditor)getInstance(PreferenceEditor.class);
                List<PreferenceValue> values = prefEditor.getPreferenceValues();
                // This is somewhat dodgy... no other way to get the value we want
                for (PreferenceValue value : values) {
                    if (value.getPreferenceProperty().getFieldName().equals("permlinkSuffix")) {
                        value.setValue(".newsuffix");
                    }
                }
                assert invokeMethod("#{adminHome.update()}") == null;
            }

        }.run();

        new NonFacesRequest("/docDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("documentId", "6");
            }

            protected void renderResponse() throws Exception {
                WikiPreferences wikiPrefs = Preferences.instance().get(WikiPreferences.class);
                assert wikiPrefs.getPermlinkSuffix().equals(".newsuffix");
            }

        }.run();
    }

    private void loginAdmin() throws Exception {
        new FacesRequest() {
           protected void invokeApplication() throws Exception {
              setValue("#{identity.username}", "admin");
              setValue("#{identity.password}", "admin");
              invokeAction("#{identity.login}");
              assert getValue("#{identity.loggedIn}").equals(true);
           }
        }.run();
    }

}
