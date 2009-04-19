/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.model;

import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiComment;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.util.WikiUtil;
import org.dbunit.operation.DatabaseOperation;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Date;

public class CommentTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void findAllCommentsFlat() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");
                WikiDocument d = (WikiDocument)
                        em.createQuery("select d from WikiDocument d where d.id = :id")
                                .setParameter("id", 6l)
                                .getSingleResult();
                assert d.getName().equals("One");

                List<WikiComment> comments =
                        em.createQuery("select c from WikiComment c where c.parent = :doc order by c.createdOn asc")
                        .setParameter("doc", d)
                        .getResultList();

                assert comments.size() == 2;

                assert comments.get(0).getName().equals("One.Comment11967298211870");
                assert comments.get(1).getSubject().equals("Five");
            }
        }.run();
    }

    @Test
    public void findAllCommentsHierachical() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");
                WikiDocument d = (WikiDocument)
                        em.createQuery("select d from WikiDocument d where d.id = :id")
                                .setParameter("id", 6l)
                                .getSingleResult();
                assert d.getName().equals("One");

                List<Object[]> result = queryComments(d, em);

                assert result.size() == 6;

                Object[] one = result.get(0);
                assert one[0].equals(1l);
                assert one[1].equals(10l);
                assert one[2].equals(1l);
                assert one[3].equals(8l);
                assert one[4].equals(10l);

                Object[] two = result.get(1);
                assert two[0].equals(2l);
                assert two[1].equals(11l);
                assert two[2].equals(2l);
                assert two[3].equals(3l);
                assert two[4].equals(10l);

                Object[] three = result.get(2);
                assert three[0].equals(2l);
                assert three[1].equals(12l);
                assert three[2].equals(4l);
                assert three[3].equals(7l);
                assert three[4].equals(10l);

                Object[] four = result.get(3);
                assert four[0].equals(3l);
                assert four[1].equals(13l);
                assert four[2].equals(5l);
                assert four[3].equals(6l);
                assert four[4].equals(10l);

                Object[] five = result.get(4);
                assert five[0].equals(1l);
                assert five[1].equals(14l);
                assert five[2].equals(1l);
                assert five[3].equals(4l);
                assert five[4].equals(14l);

                Object[] six = result.get(5);
                assert six[0].equals(2l);
                assert six[1].equals(15l);
                assert six[2].equals(2l);
                assert six[3].equals(3l);
                assert six[4].equals(14l);
            }
        }.run();
    }


    @Test(groups="jdk6-expected-failures")
    public void insertCommentNewThread() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");
                WikiDocument d = (WikiDocument)
                        em.createQuery("select d from WikiDocument d where d.id = :id")
                                .setParameter("id", 6l)
                                .getSingleResult();
                assert d.getName().equals("One");

                WikiComment newComment = new WikiComment();

                newComment.setAreaNumber(d.getAreaNumber());
                newComment.setDerivedName(d);
                newComment.setWikiname(WikiUtil.convertToWikiName(newComment.getName()));
                newComment.setCreatedBy(em.find(User.class, 1l));

                newComment.setSubject("Seven");
                newComment.setContent("Testcomment Seven");
                newComment.setUseWikiText(true);

                newComment.setParent(d);

                em.persist(newComment);

                em.flush();
                em.clear();

                List<Object[]> result = queryComments(d, em);

                assert result.size() == 7;

                Object[] one = result.get(0);
                assert one[0].equals(1l);
                assert one[1].equals(10l);
                assert one[2].equals(1l);
                assert one[3].equals(8l);
                assert one[4].equals(10l);

                Object[] two = result.get(1);
                assert two[0].equals(2l);
                assert two[1].equals(11l);
                assert two[2].equals(2l);
                assert two[3].equals(3l);
                assert two[4].equals(10l);

                Object[] three = result.get(2);
                assert three[0].equals(2l);
                assert three[1].equals(12l);
                assert three[2].equals(4l);
                assert three[3].equals(7l);
                assert three[4].equals(10l);

                Object[] four = result.get(3);
                assert four[0].equals(3l);
                assert four[1].equals(13l);
                assert four[2].equals(5l);
                assert four[3].equals(6l);
                assert four[4].equals(10l);

                Object[] five = result.get(4);
                assert five[0].equals(1l);
                assert five[1].equals(14l);
                assert five[2].equals(1l);
                assert five[3].equals(4l);
                assert five[4].equals(14l);

                Object[] six = result.get(5);
                assert six[0].equals(2l);
                assert six[1].equals(15l);
                assert six[2].equals(2l);
                assert six[3].equals(3l);
                assert six[4].equals(14l);

                Object[] seven = result.get(6);
                assert seven[0].equals(1l);
                assert seven[1].equals(newComment.getId());
                assert seven[2].equals(1l);
                assert seven[3].equals(2l);
                assert seven[4].equals(newComment.getId());

            }
        }.run();
    }


    @Test
    public void findCommentParent() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");
                WikiComment comment  = (WikiComment)
                        em.createQuery("select c from WikiComment c where c.id = :id")
                                .setParameter("id", 13l)
                                .getSingleResult();
                assert comment.getSubject().equals("Four");
                assert comment.getParentDocument().getId().equals(6l);
            }
        }.run();
    }

    private List<Object[]> queryComments(WikiDocument doc, EntityManager em) {
        String query =
            "select count(c1.id) as nslevel, c1.id, c1.nodeInfo.nsLeft, c1.nodeInfo.nsRight, c1.nodeInfo.nsThread " +
            " from WikiComment c1, WikiComment c2 " +
            " where c1.nodeInfo.nsThread = c2.nodeInfo.nsThread" +
            " and c2.nodeInfo.nsThread in (select c3.nodeInfo.nsThread from WikiComment c3 where c3.parent = :doc)" +
            " and c1.nodeInfo.nsLeft between c2.nodeInfo.nsLeft and c2.nodeInfo.nsRight" +
            " group by c1.id, c1.nodeInfo.nsLeft, c1.nodeInfo.nsRight, c1.nodeInfo.nsThread" +
            " order by c1.nodeInfo.nsThread asc, c1.nodeInfo.nsLeft asc";
        return em.createQuery(query)
                .setParameter("doc", doc)
                .getResultList();
    }

}