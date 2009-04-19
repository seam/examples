/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.model;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiMenuItem;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import java.util.List;

public class MenuTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void findAllMenuItems() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {

                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");

                WikiDirectory root = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 1l)
                                .getSingleResult();

                List<Object[]> result = queryMenuItems(root, em);

                assert result.size() == 3;

                Object[] one = result.get(0);
                assert one[0].equals(2l);
                assert one[1].equals(2l);
                assert one[2].equals(0l);

                Object[] two = result.get(1);
                assert two[0].equals(2l);
                assert two[1].equals(3l);
                assert two[2].equals(1l);

                Object[] three = result.get(2);
                assert three[0].equals(3l);
                assert three[1].equals(4l);
                assert three[2].equals(0l);

            }
        }.run();
    }


    @Test
    public void removeMenuItem() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {

                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");

                WikiDirectory d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 1l)
                                .getSingleResult();

                List<WikiMenuItem> menuItems =
                        em.createQuery("select m from WikiMenuItem m where m.id in (select d.id from WikiDirectory d where d.parent = :parent)")
                        .setParameter("parent", d).getResultList();

                assert menuItems.size() == 2;

                WikiMenuItem removedItem = menuItems.remove(0);
                em.remove(removedItem);
                for (int i = 0; i<menuItems.size(); i++) {
                    WikiMenuItem itm = menuItems.get(i);
                    itm.setDisplayPosition(i);
                }

                em.flush();
                em.clear();

                List<Object[]> result = queryMenuItems(d, em);

                assert result.size() == 2;

                Object[] one = result.get(0);
                assert one[0].equals(2l);
                assert one[1].equals(3l);
                assert one[2].equals(0l);

                Object[] two = result.get(1);
                assert two[0].equals(3l);
                assert two[1].equals(4l);
                assert two[2].equals(0l);

            }
        }.run();
    }


    @Test
    public void addMenuItem() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {

                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");

                WikiDirectory root = (WikiDirectory)
                          em.createQuery("select d from WikiDirectory d where d.id = :id")
                                  .setParameter("id", 1l)
                                  .getSingleResult();

                WikiDirectory d = (WikiDirectory)
                        em.createQuery("select d from WikiDirectory d where d.id = :id")
                                .setParameter("id", 5l)
                                .getSingleResult();

                List<WikiMenuItem> menuItems =
                        em.createQuery("select m from WikiMenuItem m where m.id in (select d.id from WikiDirectory d where d.parent = :parent)")
                        .setParameter("parent", d.getParent()).getResultList();

                WikiMenuItem newMenuItem = new WikiMenuItem(d);
                menuItems.add(newMenuItem);

                assert menuItems.size() == 2;

                for (int i = 0; i<menuItems.size(); i++) {
                    WikiMenuItem itm = menuItems.get(i);
                    itm.setDisplayPosition(i);
                }

                em.persist(newMenuItem);

                em.flush();
                em.clear();

                List<Object[]> result = queryMenuItems(root, em);

                assert result.size() == 4;

                Object[] one = result.get(0);
                assert one[0].equals(2l);
                assert one[1].equals(2l);
                assert one[2].equals(0l);

                Object[] two = result.get(1);
                assert two[0].equals(2l);
                assert two[1].equals(3l);
                assert two[2].equals(1l);

                Object[] three = result.get(2);
                assert three[0].equals(3l);
                assert three[1].equals(4l);
                assert three[2].equals(0l);

                Object[] four = result.get(3);
                assert four[0].equals(3l);
                assert four[1].equals(5l);
                assert four[2].equals(1l);

            }
        }.run();
    }

    private List<Object[]> queryMenuItems(WikiDirectory start, EntityManager em) {
        String query =
            "select count(d1.id), d1.id, m.displayPosition" +
            " from WikiDirectory d1, WikiDirectory d2, WikiMenuItem m" +
            " where d1.nodeInfo.nsThread = :thread and d2.nodeInfo.nsThread = d1.nodeInfo.nsThread" +
            " and d1.nodeInfo.nsLeft between d2.nodeInfo.nsLeft and d2.nodeInfo.nsRight" +
            " and d2.nodeInfo.nsLeft >= :left and d2.nodeInfo.nsRight <= :right" +
            " and m.id = d1.id" +
            " group by d1.id, m.displayPosition";
        return em.createQuery(query)
                .setParameter("thread", start.getNodeInfo().getNsThread())
                .setParameter("left", start.getNodeInfo().getNsLeft())
                .setParameter("right", start.getNodeInfo().getNsRight())
                .getResultList();
    }

}
