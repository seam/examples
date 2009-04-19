/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.browse;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.action.Menu;
import org.jboss.seam.wiki.core.nestedset.query.NestedSetNodeWrapper;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

public class DisplayMenu extends DBUnitSeamTest {


    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void allMenuItems() throws Exception {

        new NonFacesRequest("/wiki.xhtml") {

            protected void renderResponse() throws Exception {

                Menu menu = (Menu)getValue("#{menu}");

                assert menu.getRoot().getAdditionalProjections().get("displayPosition") == null;
                assert menu.getRoot().getWrappedNode().getId().equals(1l);
                assert menu.getRoot().getLevel().equals(0l);
                assert menu.getRoot().getWrappedChildren().size() == 2;
                for (NestedSetNodeWrapper<WikiDirectory> child : menu.getRoot().getWrappedChildrenSorted()) {
                    assert child.getLevel().equals(1l);
                }

            }

        }.run();
    }


    /*
        System.out.println("#### GOT MENU ROOT DISPLAY POSITION: " + menu.getRoot().getAdditionalProjections().get("displayPosition") + ": " + menu.getRoot());
        System.out.println("############## ROOT HAS CHILDREN: " + menu.getRoot().getWrappedChildren().size());

        for (NestedSetNodeWrapper<WikiDirectory> child : menu.getRoot().getWrappedChildrenSorted()) {

            System.out.println("#### CHILD DISPLAY POSITION: " + child.getAdditionalProjections().get("displayPosition") + ": " + child);

            for (NestedSetNodeWrapper<WikiDirectory> child2 : child.getWrappedChildrenSorted()) {
                System.out.println("####### CHILD2 DISPLAY POSITION: " + child.getAdditionalProjections().get("displayPosition") + ": " + child2);

            }
        }
     */
}