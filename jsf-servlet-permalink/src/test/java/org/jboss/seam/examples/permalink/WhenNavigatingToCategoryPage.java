package org.jboss.seam.examples.permalink;

import java.util.List;

import org.jboss.seam.test.AbstractScenario;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Dan Allen
 */
public class WhenNavigatingToCategoryPage extends AbstractScenario {
    private static final String CATEGORY_PAGE = "/category.xhtml";

    @Test(groups = {"integration", "scenario"})
    public void shouldSetCategoryFromViewParameter() throws Exception {
        tester.requestPage(CATEGORY_PAGE + "?name=General");
        String category = getValue("blog.category", String.class);
        assertEquals(category, "General");
    }

    @Test(groups = {"integration", "scenario"})
    public void shouldDisplayEntriesInCategory() throws Exception {
        tester.requestPage(CATEGORY_PAGE + "?name=JSF 2");
        List<BlogEntry> entriesForPage = getValue("blog.entriesForPage", List.class);
        assertNotNull(entriesForPage);
        assertEquals(entriesForPage.size(), 3);
        for (BlogEntry entry : entriesForPage) {
            assertEquals(entry.getCategory(), "JSF 2");
        }
    }
}
