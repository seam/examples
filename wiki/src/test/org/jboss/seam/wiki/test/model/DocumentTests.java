/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.model;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;

public class DocumentTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void loadDocumentById() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");
                WikiDocument d = (WikiDocument)
                        em.createQuery("select d from WikiDocument d where d.id = :id")
                                .setParameter("id", 6l)
                                .getSingleResult();
                assert d.getName().equals("One");
            }
        }.run();
    }

    @Test
    public void updateDocumentById() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");
                WikiDocument d = (WikiDocument)
                        em.createQuery("select d from WikiDocument d where d.id = :id")
                                .setParameter("id", 6l)
                                .getSingleResult();
                d.setName("One2");
                em.flush();

                em.clear();
                d = (WikiDocument)
                        em.createQuery("select d from WikiDocument d where d.id = :id")
                                .setParameter("id", 6l)
                                .getSingleResult();
                assert d.getName().equals("One2");
                assert d.getLastModifiedBy() != null;
                assert d.getLastModifiedOn() != null;
            }
        }.run();
    }

    @Test
    public void deleteDocumentById() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");

                WikiDocument d = (WikiDocument)
                        em.createQuery("select d from WikiDocument d left join fetch d.incomingLinks where d.id = :id")
                                .setParameter("id", 8l)
                                .getSingleResult();
                assert d.getIncomingLinks().size() == 1;

                em.clear();

                d = (WikiDocument)
                        em.createQuery("select d from WikiDocument d left join fetch d.incomingLinks where d.id = :id")
                                .setParameter("id", 9l)
                                .getSingleResult();
                assert d.getIncomingLinks().size() == 1;

                em.clear();

                d = (WikiDocument)
                        em.createQuery("select d from WikiDocument d where d.id = :id")
                                .setParameter("id", 7l)
                                .getSingleResult();

                assert d.getOutgoingLinks().size() == 2;

                em.remove(d);
                em.flush();

                try {
                    d = null;
                    d = (WikiDocument)
                            em.createQuery("select d from WikiDocument d where d.id = :id")
                                    .setParameter("id", 7l)
                                    .getSingleResult();
                } catch (Exception ex) {} finally {
                    assert d == null;
                }

                em.clear();

                d = (WikiDocument)
                        em.createQuery("select d from WikiDocument d left join fetch d.incomingLinks where d.id = :id")
                                .setParameter("id", 8l)
                                .getSingleResult();
                assert d.getIncomingLinks().size() == 0;

                em.clear();

                d = (WikiDocument)
                        em.createQuery("select d from WikiDocument d left join fetch d.incomingLinks where d.id = :id")
                                .setParameter("id", 9l)
                                .getSingleResult();
                assert d.getIncomingLinks().size() == 0;

            }
        }.run();
    }

    @Test(groups="jdk6-expected-failures")
    public void insertDocumentById() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");
                WikiDirectory d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 3l)
                                .getSingleResult();

                WikiDocument newDoc = new WikiDocument();
                newDoc.setName("Four");
                newDoc.setWikiname("Four");
                newDoc.setAreaNumber(d.getAreaNumber());
                newDoc.setCreatedBy(em.find(User.class, 1l));
                newDoc.setContent("Testdocument Four");
                newDoc.setParent(d);

                em.persist(newDoc);
                em.flush();

                em.clear();
                WikiDocument doc = (WikiDocument)
                        em.createQuery("select d from WikiDocument d where d.id = :id")
                                .setParameter("id", newDoc.getId())
                                .getSingleResult();
                assert doc.getName().equals("Four");
                assert doc.getParent().getId().equals(d.getId());
                assert doc.getAreaNumber().equals(d.getAreaNumber());
                assert doc.getLastModifiedBy() == null;
                assert doc.getLastModifiedOn() == null;
            }
        }.run();
    }

}
