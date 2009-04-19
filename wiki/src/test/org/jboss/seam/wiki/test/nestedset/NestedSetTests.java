/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.nestedset;

import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.jboss.seam.wiki.core.nestedset.NestedSetNode;
import org.jboss.seam.wiki.core.nestedset.query.NestedSetQueryBuilder;
import org.jboss.seam.wiki.core.nestedset.query.NestedSetNodeWrapper;
import org.jboss.seam.wiki.core.nestedset.query.NestedSetResultTransformer;
import org.testng.annotations.Test;
import org.hibernate.Session;
import org.hibernate.Query;
import org.hibernate.ejb.HibernateEntityManagerFactory;

import javax.persistence.EntityManager;
import java.util.Comparator;

public class NestedSetTests extends DBUnitSeamTest {

    private Log log = Logging.getLog(NestedSetTests.class);

    protected void prepareDBUnitOperations() {
        // Don't need any datasets
    }

    @Test
    public void insertDeleteOnTree() throws Exception {

        new FacesRequest("/") {

            protected void invokeApplication() throws Exception {

                EntityManager em = (EntityManager)getValue("#{entityManager}");

                /* Tree:
                          A
                         / \
                        B   C
                       / \
                      F1  F2
                     /  \
                 F1Sub F1DirA
                         |
                       F1DirB
                 */
                TestDirectory a = new TestDirectory("A");
                TestDirectory b = new TestDirectory("B");
                TestDirectory c = new TestDirectory("C");

                a.addChild(b);
                a.addChild(c);

                em.persist(a);
                em.persist(b);
                em.persist(c);

                em.flush();

                TestFile f1 = new TestFile("F1");
                f1.setFilename("file1.txt");
                TestFile f2 = new TestFile("F2");
                f2.setFilename("file2.txt");

                b.addChild(f1);
                b.addChild(f2);

                em.persist(f1);
                em.persist(f2);

                em.flush();

                TestFile f1Subitem = new TestFile("F1Sub");
                f1Subitem.setFilename("f1subfile.txt");
                f1.addChild(f1Subitem);
                em.persist(f1Subitem);

                TestDirectory f1DirA = new TestDirectory("F1DirA");
                f1.addChild(f1DirA);
                em.persist(f1DirA);

                TestDirectory f1DirB = new TestDirectory("F1DirB");
                f1DirA.addChild(f1DirB);
                em.persist(f1DirB);

                getUserTransaction().commit();
                em.clear();

                showDirTree("A");
                long firstThread = a.getNodeInfo().getNsThread();
                assert checkTestDirectoryInDatabase("A", firstThread, 1, 6);
                assert checkTestDirectoryInDatabase("B", firstThread, 2, 3);
                assert checkTestDirectoryInDatabase("C", firstThread, 4, 5);

                showDirTree("F1DirA");
                long secondThread = f1DirA.getNodeInfo().getNsThread();
                assert checkTestDirectoryInDatabase("F1DirA", secondThread, 1, 4);
                assert checkTestDirectoryInDatabase("F1DirB", secondThread, 2, 3);

                /* Tree:
                          A
                         /
                        B
                       / \
                      F1  F2
                     /  \
                 F1Sub F1DirA
                         |
                       F1DirB
                 */
                getUserTransaction().begin();
                em.joinTransaction();

                TestDirectory dirToDelete = getTestDirectory( (Session)em.getDelegate(), "C");

                em.remove(dirToDelete);

                getUserTransaction().commit();
                em.clear();

                showDirTree("A");
                long thread = a.getNodeInfo().getNsThread();
                assert checkTestDirectoryInDatabase("A", thread, 1, 4);
                assert checkTestDirectoryInDatabase("B", thread, 2, 3);

                /* Tree:
                          A
                          |
                          B
                          |
                          F2
                 */

                getUserTransaction().begin();
                em.joinTransaction();

                TestFile fileToDelete = em.find(TestFile.class, f1.getId());

                em.remove(fileToDelete);

                getUserTransaction().commit();
                em.clear();

                assert getTestDirectory("F1DirA") == null;
                assert getTestDirectory("F1DirB") == null;

            }
        }.run();

    }

    /* ############################################################################################################## */

    private boolean checkNestedSetNodeInMemory(NestedSetNode node, long thread, long left, long right) throws Exception {
        return node.getNodeInfo().getNsThread() == thread
                && node.getNodeInfo().getNsLeft() == left
                && node.getNodeInfo().getNsRight() == right;
    }

    private boolean checkTestDirectoryInDatabase(String dirName, long thread, long left, long right) throws Exception {
        return checkNestedSetNodeInMemory( getTestDirectory(dirName), thread, left, right);
    }

    private TestDirectory getTestDirectory(String dirName) throws Exception {
        Session session = getHibernateSession();
        TestDirectory dir = getTestDirectory(session, dirName);
        session.close();
        return dir;
    }

    private TestDirectory getTestDirectory(Session session, String dirName) throws Exception {
        return (TestDirectory) session
                .createQuery("select d from TestDirectory d where d.name = :name")
                .setParameter("name", dirName).uniqueResult();
    }

    private void showDirTree(String startDirName) throws Exception {
        if (log.isTraceEnabled()) {

            Session session = getHibernateSession();

            TestDirectory startDir = getTestDirectory(session, startDirName);
            if (startDir == null) {
                throw new RuntimeException("Could not find start dir with name: " + startDirName);
            }

            NestedSetQueryBuilder nsQuery = new NestedSetQueryBuilder(startDir);
            Query nestedSetQuery =  session.createQuery(nsQuery.getSimpleQuery());
            nestedSetQuery.setParameter("nsThread", startDir.getNodeInfo().getNsThread());
            nestedSetQuery.setParameter("nsLeft", startDir.getNodeInfo().getNsLeft());
            nestedSetQuery.setParameter("nsRight", startDir.getNodeInfo().getNsRight());

            // This comparator sorts the TestItems by name!
            Comparator<NestedSetNodeWrapper<TestDirectory>> comp =
                new Comparator<NestedSetNodeWrapper<TestDirectory>>() {
                    public int compare(NestedSetNodeWrapper<TestDirectory> o, NestedSetNodeWrapper<TestDirectory> o2) {
                        return o.getWrappedNode().getName().compareTo(o2.getWrappedNode().getName());
                    }
                };

            NestedSetNodeWrapper<TestDirectory> startNodeWrapper
                    = new NestedSetNodeWrapper<TestDirectory>(startDir, comp);
            nestedSetQuery.setResultTransformer( new NestedSetResultTransformer<TestDirectory>(startNodeWrapper) );

            nestedSetQuery.list(); // Append all children hierarchically to the startNodeWrapper

            log.trace("######################################## TREE BEGIN #####################################################");
            renderDirTree(startNodeWrapper);
            log.trace("######################################## TREE END   #####################################################");

            session.close();
        }
    }
    private void renderDirTree(NestedSetNodeWrapper<TestDirectory> startNode) {
        StringBuilder levelMarkers = new StringBuilder();
        for (int i = 1; i <= startNode.getLevel(); i++) {
            levelMarkers.append("#");
        }
        log.trace(levelMarkers.toString() + " " + startNode);
        for (NestedSetNodeWrapper<TestDirectory> next : startNode.getWrappedChildren()) {
            renderDirTree(next);
        }
    }


    private Session getHibernateSession() throws Exception {
        org.jboss.ejb3.entity.InjectedEntityManagerFactory jbossEMF =
                (org.jboss.ejb3.entity.InjectedEntityManagerFactory) getInitialContext().lookup("java:/entityManagerFactories/wiki");
        return ((HibernateEntityManagerFactory) jbossEMF.getDelegate()).getSessionFactory().openSession();
    }

}
