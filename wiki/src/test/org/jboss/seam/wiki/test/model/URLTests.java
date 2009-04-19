/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.model;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;

public class URLTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/UploadData.dbunit.xml", DatabaseOperation.INSERT)
        );
    }

    @Test
    public void documentURL() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");
                WikiDocument d = (WikiDocument)
                        em.createQuery("select d from WikiDocument d where d.id = :id")
                                .setParameter("id", 6l)
                                .getSingleResult();

                assert d.getPermURL(".lace").equals("6.lace");
                assert d.getWikiURL().equals("CCC/One");
            }
        }.run();
    }

    @Test
    public void directoryURL() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");
                WikiDirectory d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 3l)
                                .getSingleResult();

                assert d.getPermURL(".lace").equals("3.lace");
                assert d.getWikiURL().equals("CCC");

                d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 4l)
                                .getSingleResult();

                assert d.getPermURL(".lace").equals("4.lace");
                assert d.getWikiURL().equals("CCC/DDD");

                d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 1l)
                                .getSingleResult();

                assert d.getPermURL(".lace").equals("1.lace");
                assert d.getWikiURL().equals("");

            }
        }.run();
    }

    @Test
    public void commentURL() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");
                WikiComment c = (WikiComment)
                        em.createQuery("select c from WikiComment c where c.id = :id")
                                .setParameter("id", 10l)
                                .getSingleResult();

                assert c.getPermURL(".lace").equals("6.lace#comment10");
                assert c.getWikiURL().equals("CCC/One#comment10");

            }
        }.run();
    }


    @Test
    public void uploadURL() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");
                WikiUpload u = (WikiUpload)
                        em.createQuery("select u from WikiUpload u where u.id = :id")
                                .setParameter("id", 30l)
                                .getSingleResult();

                assert u.getPermURL(".lace").equals("service/File/30");
                assert u.getWikiURL().equals("service/File/30");

            }
        }.run();
    }

}
