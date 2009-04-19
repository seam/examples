/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.preferences;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.action.PreferenceEditor;
import org.jboss.seam.wiki.core.action.prefs.DocumentEditorPreferences;
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
public class UserPreferencesTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test(groups="jdk6-expected-failures")
    public void createUserPreferences() throws Exception {

        loginMember();

        final String conversationId = new NonFacesRequest("/userHome_d.xhtml") {
            protected void beforeRequest() {
                setParameter("userId", "3");
            }

        }.run();

        new FacesRequest("/userHome_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {

                PreferenceRegistry registry = (PreferenceRegistry)getInstance(PreferenceRegistry.class);
                PreferenceEntity docEditorEntity = registry.getPreferenceEntitiesByName().get("DocEditor");

                invokeMethod("#{userHome.initPreferencesEditor}");

                PreferenceEditor prefEditor = (PreferenceEditor)getInstance(PreferenceEditor.class);
                prefEditor.selectPreferenceEntity(docEditorEntity);
            }

        }.run();

        new FacesRequest("/userHome_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                PreferenceEditor prefEditor = (PreferenceEditor)getInstance(PreferenceEditor.class);
                List<PreferenceValue> values = prefEditor.getPreferenceValues();
                // This is somewhat dodgy... no other way to get the value we want
                for (PreferenceValue value : values) {
                    if (value.getPreferenceProperty().getFieldName().equals("minorRevisionEnabled")) {
                        assert value.getValue().equals(Boolean.TRUE); // Should be the system setting
                        value.setValue(false);
                    }
                }
                assert invokeMethod("#{userHome.update()}").equals("updated");
            }

        }.run();

        new NonFacesRequest("/docDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("documentId", "6");
            }

            protected void renderResponse() throws Exception {
                DocumentEditorPreferences docEditorPrefs = Preferences.instance().get(DocumentEditorPreferences.class);
                assert !docEditorPrefs.getMinorRevisionEnabled();
            }

        }.run();


        // Logout and check system setting, should still be true
        logout();

        new NonFacesRequest("/docDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("documentId", "6");
            }

            protected void renderResponse() throws Exception {
                DocumentEditorPreferences docEditorPrefs = Preferences.instance().get(DocumentEditorPreferences.class);
                assert docEditorPrefs.getMinorRevisionEnabled();
            }

        }.run();

    }

    private void loginMember() throws Exception {
        new FacesRequest() {
           protected void invokeApplication() throws Exception {
              setValue("#{identity.username}", "member");
              setValue("#{identity.password}", "member");
              invokeAction("#{identity.login}");
              assert getValue("#{identity.loggedIn}").equals(true);
           }
        }.run();
    }

    private void logout() throws Exception {
        new FacesRequest() {
           protected void invokeApplication() throws Exception {
               invokeMethod("#{identity.logout}");
           }
        }.run();
    }

}