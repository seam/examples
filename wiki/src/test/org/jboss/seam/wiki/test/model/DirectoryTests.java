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
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;

public class DirectoryTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void loadDirectoryById() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");
                WikiDirectory d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 1l)
                                .getSingleResult();
                assert d.getName().equals("AAA");
                assert d.getNodeInfo().getNsLeft().equals(1l);
                assert d.getNodeInfo().getNsRight().equals(999l);
            }
        }.run();
    }

    @Test
    public void getDirectoryPath() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");
                WikiDirectory d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 4l)
                                .getSingleResult();
                assert d.getName().equals("DDD");
                assert d.getPath().size() == 3;
                assert d.getPath().get(0).getId().equals(1l);
                assert d.getPath().get(1).getId().equals(3l);
                assert d.getPath().get(2).getId().equals(4l);
            }
        }.run();
    }

    @Test
    public void updateDirectoryById() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");
                WikiDirectory d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 1l)
                                .getSingleResult();
                d.setName("AAA2");
                em.flush();

                em.clear();
                d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 1l)
                                .getSingleResult();
                assert d.getName().equals("AAA2");
                assert d.getNodeInfo().getNsLeft().equals(1l);
                assert d.getNodeInfo().getNsRight().equals(999l);
            }
        }.run();
    }

    /* TODO: Requires (now deprecated) database cascading, deletion nees to go through DirectoryHome
    @Test
    public void deleteDirectoryById() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");
                WikiDirectory d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 4l)
                                .getSingleResult();
                em.remove(d);
                em.flush();

                em.clear();
                d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 1l)
                                .getSingleResult();
                assert d.getName().equals("AAA");
                assert d.getNodeInfo().getNsLeft().equals(1l);
                assert d.getNodeInfo().getNsRight().equals(997l);

                em.clear();
                d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 3l)
                                .getSingleResult();
                assert d.getName().equals("CCC");
                assert d.getNodeInfo().getNsLeft().equals(4l);
                assert d.getNodeInfo().getNsRight().equals(7l);

                em.clear();
                d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 5l)
                                .getSingleResult();
                assert d.getName().equals("EEE");
                assert d.getNodeInfo().getNsLeft().equals(5l);
                assert d.getNodeInfo().getNsRight().equals(6l);
            }
        }.run();
    }

    @Test
    public void deleteDirectoryTreeById() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");
                WikiDirectory d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 3l)
                                .getSingleResult();
                em.remove(d);
                em.flush();

                em.clear();
                d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 1l)
                                .getSingleResult();
                assert d.getName().equals("AAA");
                assert d.getNodeInfo().getNsLeft().equals(1l);
                assert d.getNodeInfo().getNsRight().equals(993l);

                em.clear();
                d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 2l)
                                .getSingleResult();
                assert d.getName().equals("BBB");
                assert d.getNodeInfo().getNsLeft().equals(2l);
                assert d.getNodeInfo().getNsRight().equals(3l);

                em.clear();
                try {
                    d = null;
                    d = (WikiDirectory)
                            em.createQuery("select d from WikiDirectory d where d.id = :id")
                                    .setParameter("id", 3l)
                                    .getSingleResult();
                } catch (Exception ex) {} finally {
                    assert d == null;
                }

                em.clear();
                try {
                    d = null;
                    d = (WikiDirectory)
                            em.createQuery("select d from WikiDirectory d where d.id = :id")
                                    .setParameter("id", 4l)
                                    .getSingleResult();
                } catch (Exception ex) {} finally {
                    assert d == null;
                }

                em.clear();
                try {
                    d = null;
                    d = (WikiDirectory)
                            em.createQuery("select d from WikiDirectory d where d.id = :id")
                                    .setParameter("id", 5l)
                                    .getSingleResult();
                } catch (Exception ex) {} finally {
                    assert d == null;
                }
            }
        }.run();
    }
    */

    @Test(groups="jdk6-expected-failures")
    public void insertDirectoryById() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");
                WikiDirectory d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 3l)
                                .getSingleResult();

                WikiDirectory newDir = new WikiDirectory();
                newDir.setName("FFF");
                newDir.setWikiname("FFF");
                newDir.setAreaNumber(d.getAreaNumber());
                newDir.setCreatedBy(em.find(User.class, 1l));
                newDir.setParent(d);

                em.persist(newDir);
                em.flush();

                em.clear();
                d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 1l)
                                .getSingleResult();
                assert d.getName().equals("AAA");
                assert d.getNodeInfo().getNsLeft().equals(1l);
                assert d.getNodeInfo().getNsRight().equals(1001l);

                em.clear();
                d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 3l)
                                .getSingleResult();
                assert d.getName().equals("CCC");
                assert d.getNodeInfo().getNsLeft().equals(4l);
                assert d.getNodeInfo().getNsRight().equals(11l);

                em.clear();
                d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", newDir.getId())
                                .getSingleResult();
                assert d.getName().equals("FFF");
                assert d.getNodeInfo().getNsLeft().equals(9l);
                assert d.getNodeInfo().getNsRight().equals(10l);
            }
        }.run();
    }



}
