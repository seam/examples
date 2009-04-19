/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.model;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.action.DocumentHome;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiDocumentDefaults;
import org.jboss.seam.wiki.core.model.WikiTextMacro;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Bauer
 */
public class DocumentMacroTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void modifyMacros() throws Exception {
        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("nodeId", "6");
            }

            protected void renderResponse() throws Exception {
                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                assert docHome.getInstance().getId().equals(6l); // Init!

                WikiDocument doc = docHome.getInstance();

                // Header
                doc.setHeader("[<=testMacro1[param1=value1]]\n[<=testMacro2]\nfoo");
                docHome.syncMacros(doc);
                assert doc.getHeaderMacros().size() == 2;
                assert doc.getHeaderMacrosString().contains("testMacro1");
                assert doc.getHeaderMacrosString().contains("testMacro2");
                assert doc.getHeader().contains("[<=testMacro1[param1=value1]]\n");
                assert doc.getHeader().contains("[<=testMacro2]\n");
                assert doc.getHeader().indexOf("foo") == doc.getHeader().length()-3;

                doc.removeHeaderMacros("testMacro1");
                WikiTextMacro replacement = new WikiTextMacro("testMacro3");
                replacement.getParams().put("p1", "value1");
                replacement.getParams().put("p2", "value2");
                doc.addHeaderMacro(replacement);
                assert doc.getHeaderMacros().size() == 2;
                assert doc.getHeaderMacrosString().contains("testMacro3");
                assert doc.getHeaderMacrosString().contains("testMacro2");
                assert doc.getHeader().contains("[<=testMacro3[p1=value1][p2=value2]]\n");
                assert doc.getHeader().contains("[<=testMacro2]\n");
                assert doc.getHeader().indexOf("foo") == doc.getHeader().length()-3;


            }

        }.run();
    }
    
    @Test
    public void macrosFromDefaultStrings() throws Exception {
        WikiDocument doc = new WikiDocument();

        WikiDocumentDefaults defaults = new WikiDocumentDefaults() {
            @Override
            public String[] getContentMacrosAsString() {
                return new String[] {"contentMacro1", "contentMacro2"};
            }
            @Override
            public String getContentText() {
                return "foo";
            }
            @Override
            public String[] getHeaderMacrosAsString() {
                return new String[] {"headerMacro1", "headerMacro2"};
            }
            @Override
            public String getHeaderText() {
                return "bar";
            }
            @Override
            public String[] getFooterMacrosAsString() {
                return new String[] {"footerMacro1", "footerMacro2"};
            }
            @Override
            public String getFooterText() {
                return "baz";
            }
        };
        doc.setDefaults(defaults);

        assert doc.getContentMacros().size() == 2;
        assert doc.getContentMacrosString().contains("contentMacro1");
        assert doc.getContentMacrosString().contains("contentMacro1");
        assert doc.getContent().contains("[<=contentMacro1]");
        assert doc.getContent().contains("[<=contentMacro2]");
        assert doc.getContent().indexOf("foo") == doc.getContent().length()-3;

        assert doc.getHeaderMacros().size() == 2;
        assert doc.getHeaderMacrosString().contains("headerMacro1");
        assert doc.getHeaderMacrosString().contains("headerMacro2");
        assert doc.getHeader().contains("[<=headerMacro1]");
        assert doc.getHeader().contains("[<=headerMacro2]");
        assert doc.getHeader().indexOf("bar") == doc.getHeader().length()-3;

        assert doc.getFooterMacros().size() == 2;
        assert doc.getFooterMacrosString().contains("footerMacro1");
        assert doc.getFooterMacrosString().contains("footerMacro2");
        assert doc.getFooter().contains("[<=footerMacro1]");
        assert doc.getFooter().contains("[<=footerMacro2]");
        assert doc.getFooter().indexOf("baz") == 0;
    }

    @Test
    public void macrosFromDefaultTypesafe() throws Exception {
        WikiDocument doc = new WikiDocument();

        WikiDocumentDefaults defaults = new WikiDocumentDefaults() {
            @Override
            public List<WikiTextMacro> getContentMacros() {
                return new ArrayList<WikiTextMacro>() {{
                   add(new WikiTextMacro("contentMacro1", 0));
                   add(new WikiTextMacro("contentMacro2", 1));
                }};
            }
            @Override
            public String getContentText() {
                return "foo";
            }
            @Override
            public List<WikiTextMacro> getHeaderMacros() {
                return new ArrayList<WikiTextMacro>() {{
                   add(new WikiTextMacro("headerMacro1", 0));
                   add(new WikiTextMacro("headerMacro2", 1));
                }};
            }
            @Override
            public String getHeaderText() {
                return "bar";
            }
            @Override
            public List<WikiTextMacro> getFooterMacros() {
                return new ArrayList<WikiTextMacro>() {{
                   add(new WikiTextMacro("footerMacro1", 0));
                   add(new WikiTextMacro("footerMacro2", 1));
                }};
            }
            @Override
            public String getFooterText() {
                return "baz";
            }
        };
        doc.setDefaults(defaults);

        assert doc.getContentMacros().size() == 2;
        assert doc.getContentMacrosString().contains("contentMacro1");
        assert doc.getContentMacrosString().contains("contentMacro1");
        assert doc.getContent().contains("[<=contentMacro1]");
        assert doc.getContent().contains("[<=contentMacro2]");
        assert doc.getContent().indexOf("foo") == doc.getContent().length()-3;

        assert doc.getHeaderMacros().size() == 2;
        assert doc.getHeaderMacrosString().contains("headerMacro1");
        assert doc.getHeaderMacrosString().contains("headerMacro2");
        assert doc.getHeader().contains("[<=headerMacro1]");
        assert doc.getHeader().contains("[<=headerMacro2]");
        assert doc.getHeader().indexOf("bar") == doc.getHeader().length()-3;

        assert doc.getFooterMacros().size() == 2;
        assert doc.getFooterMacrosString().contains("footerMacro1");
        assert doc.getFooterMacrosString().contains("footerMacro2");
        assert doc.getFooter().contains("[<=footerMacro1]");
        assert doc.getFooter().contains("[<=footerMacro2]");
        assert doc.getFooter().indexOf("baz") == 0;
    }

}
