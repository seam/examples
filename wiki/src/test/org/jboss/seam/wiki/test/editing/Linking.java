/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.editing;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.action.DocumentHome;
import org.jboss.seam.wiki.core.wikitext.engine.WikiLink;
import org.jboss.seam.wiki.core.wikitext.engine.WikiLinkResolver;
import org.jboss.seam.wiki.core.wikitext.renderer.WikiTextRenderer;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiUpload;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.jboss.seam.wiki.util.WikiUtil;
import org.testng.annotations.Test;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Linking extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/UploadData.dbunit.xml", DatabaseOperation.INSERT)
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/HelpDocuments.dbunit.xml", DatabaseOperation.INSERT)
        );
    }

    @Test
    public void linkToKnownProtocols() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("documentId", "6");
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                assert docHome.getInstance().getId().equals(6l); // Init!

                docHome.getTextEditor().setValue("[=>http://foo.bar]");
                docHome.syncEditorToInstance(docHome.getParentNode().getId(), docHome.getInstance());
                assert docHome.getInstance().getContent().equals("[=>http://foo.bar]");

                docHome.getTextEditor().setValue("[=>https://foo.bar]");
                docHome.syncEditorToInstance(docHome.getParentNode().getId(), docHome.getInstance());
                assert docHome.getInstance().getContent().equals("[=>https://foo.bar]");

                docHome.getTextEditor().setValue("[=>ftp://foo.bar]");
                docHome.syncEditorToInstance(docHome.getParentNode().getId(), docHome.getInstance());
                assert docHome.getInstance().getContent().equals("[=>ftp://foo.bar]");

                docHome.getTextEditor().setValue("[=>mailto:foo@bar.tld]");
                docHome.syncEditorToInstance(docHome.getParentNode().getId(), docHome.getInstance());
                assert docHome.getInstance().getContent().equals("[=>mailto:foo@bar.tld]");

                docHome.getTextEditor().setValue("[Foo Bar=>http://foo.bar]");
                docHome.syncEditorToInstance(docHome.getParentNode().getId(), docHome.getInstance());
                assert docHome.getInstance().getContent().equals("[Foo Bar=>http://foo.bar]");

                docHome.getTextEditor().setValue("[Foo Bar=>https://foo.bar]");
                docHome.syncEditorToInstance(docHome.getParentNode().getId(), docHome.getInstance());
                assert docHome.getInstance().getContent().equals("[Foo Bar=>https://foo.bar]");

                docHome.getTextEditor().setValue("[Foo Bar=>ftp://foo.bar]");
                docHome.syncEditorToInstance(docHome.getParentNode().getId(), docHome.getInstance());
                assert docHome.getInstance().getContent().equals("[Foo Bar=>ftp://foo.bar]");

                docHome.getTextEditor().setValue("[Foo Bar=>mailto:foo@bar.tld]");
                docHome.syncEditorToInstance(docHome.getParentNode().getId(), docHome.getInstance());
                assert docHome.getInstance().getContent().equals("[Foo Bar=>mailto:foo@bar.tld]");

            }

        }.run();
    }

    @Test
    public void linkToCustomProtocols() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("documentId", "6");
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                assert docHome.getInstance().getId().equals(6l); // Init!

                WikiLinkResolver resolver = (WikiLinkResolver)getInstance("wikiLinkResolver");
                Map<String, WikiLink> links = new HashMap<String, WikiLink>();

                docHome.getTextEditor().setValue("[=>hhh://1234]");
                resolver.resolveLinkText(3l, links, "hhh://1234");
                assert links.size()==1;
                assert links.get("hhh://1234").getUrl().equals("http://opensource.atlassian.com/projects/hibernate/browse/HHH-1234");
            }

        }.run();
    }

    @Test
    public void linkToDocuments() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("documentId", "6");
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                assert docHome.getInstance().getId().equals(6l); // Init!

                WikiLinkResolver resolver = (WikiLinkResolver)getInstance("wikiLinkResolver");

                checkLink(resolver, 7l, "[=>Two]", "[=>wiki://7]");
                checkLink(resolver, 7l, "[Foo Bar=>Two]", "[Foo Bar=>wiki://7]");

                checkLink(resolver, 9l, "[=>BBB|Four]", "[=>wiki://9]");
                checkLink(resolver, 9l, "[Foo Bar=>BBB|Four]", "[Foo Bar=>wiki://9]");

                checkLink(resolver, null, "[=>Four]", "[=>Four]"); // Broken link
                checkLink(resolver, null, "[Foo Bar=>Four]", "[Foo Bar=>Four]"); // Broken link

            }

        }.run();
    }

    @Test
    public void linkToDocumentFragments() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("documentId", "6");
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                assert docHome.getInstance().getId().equals(6l); // Init!

                WikiLinkResolver resolver = (WikiLinkResolver)getInstance("wikiLinkResolver");

                final String FRAGMENT = "#foo123.,; baz -?!()/&";

                checkLink(resolver, 7l, "[=>Two"+FRAGMENT+"]", "[=>wiki://7"+FRAGMENT+"]", FRAGMENT);
                checkLink(resolver, 9l, "[=>BBB|Four"+FRAGMENT+"]", "[=>wiki://9"+FRAGMENT+"]", FRAGMENT);
            }

        }.run();
    }

    @Test
    public void linkToUploads() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("documentId", "6");
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                assert docHome.getInstance().getId().equals(6l); // Init!

                WikiLinkResolver resolver = (WikiLinkResolver)getInstance("wikiLinkResolver");

                checkLink(resolver, 30l, "[=>BBB|Test Image]", "[=>wiki://30]");
            }

        }.run();
    }

    @Test
    public void linkSourceTargetTracking() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("documentId", "6");
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                assert docHome.getInstance().getId().equals(6l); // Init!

                assert docHome.getInstance().getOutgoingLinks().size() == 0;
                docHome.getTextEditor().setValue("[=>Two] and [=>Three] and [=>BBB|Test Image]");

                assert invokeMethod("#{documentHome.update}").equals("updated");

                assert docHome.getInstance().getContent().equals("[=>wiki://7] and [=>wiki://8] and [=>wiki://30]");
                assert docHome.getInstance().getOutgoingLinks().size() == 3;

            }

        }.run();

        new FacesRequest() {

            protected void invokeApplication() throws Exception {
                EntityManager em = (EntityManager) getInstance("restrictedEntityManager");

                WikiDocument d = (WikiDocument)
                        em.createQuery("select d from WikiDocument d left join fetch d.incomingLinks where d.id = :id")
                                .setParameter("id", 7l)
                                .getSingleResult();
                assert d.getIncomingLinks().size() == 1;
                em.clear();

                d = (WikiDocument)
                        em.createQuery("select d from WikiDocument d left join fetch d.incomingLinks where d.id = :id")
                                .setParameter("id", 8l)
                                .getSingleResult();
                assert d.getIncomingLinks().size() == 2;
                em.clear();

                WikiUpload f = (WikiUpload)
                        em.createQuery("select f from WikiUpload f left join fetch f.incomingLinks where f.id = :id")
                                .setParameter("id", 30l)
                                .getSingleResult();
                assert f.getIncomingLinks().size() == 1;
            }
        }.run();

    }

    private void checkLink(WikiLinkResolver resolver, Long fileId, String wikiText, String databaseText) {
        checkLink(resolver, fileId, wikiText, databaseText, null);
    }

    private void checkLink(WikiLinkResolver resolver, Long fileId, String wikiText, String databaseText, String fragment) {
        assert resolver.convertToWikiProtocol(new HashSet(), 3l, wikiText).equals(databaseText);
        assert resolver.convertFromWikiProtocol(3l, databaseText).equals(wikiText);
        Map<String, WikiLink> links = new HashMap<String, WikiLink>();
        resolver.resolveLinkText(3l, links, databaseText);
        assert links.size()==1;
        if (fileId == null) {
            assert links.get(databaseText).isBroken();
        } else {
            assert links.get(databaseText).getFile().getId().equals(fileId);
            if (fragment != null) {
                assert links.get(databaseText).getFragment().equals(fragment);
                assert links.get(databaseText).getEncodedFragment().equals(
                    WikiTextRenderer.HEADLINE_ID_PREFIX+WikiUtil.convertToWikiName(fragment)
                );
            }
        }

    }


}