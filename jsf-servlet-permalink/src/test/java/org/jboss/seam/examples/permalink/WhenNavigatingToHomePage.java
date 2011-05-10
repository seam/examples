package org.jboss.seam.examples.permalink;

import java.util.List;

import javax.faces.component.UICommand;

import com.steeplesoft.jsf.facestester.FacesComponent;
import com.steeplesoft.jsf.facestester.FacesPage;
import org.jboss.seam.test.AbstractScenario;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Dan Allen
 */
public class WhenNavigatingToHomePage extends AbstractScenario {
    private static final String HOME_PAGE = "/home.xhtml";

    @Test(groups = {"integration", "scenario"})
    public void shouldLoadBlogEntries() throws Exception {
        tester.requestPage(HOME_PAGE);
        List<BlogEntry> entriesForPage = getValue("blog.entriesForPage", List.class);
        assertNotNull(entriesForPage);
        assertEquals(entriesForPage.size(), 3);
    }

    @Test(groups = {"integration", "scenario"})
    public void shouldHaveLinksDisabledToFirstAndPreviousPage() throws Exception {
        FacesPage homePage = tester.requestPage(HOME_PAGE);
        FacesComponent first = homePage.getComponentWithId("first");
        assertEquals(first.getValueAsString(), "First Page");
        assertTrue(Boolean.valueOf(String.valueOf(first.getWrappedComponent().getAttributes().get("disabled"))));
        FacesComponent previous = homePage.getComponentWithId("previous");
        assertEquals(previous.getValueAsString(), "Newer Entries");
        assertTrue(Boolean.valueOf(String.valueOf(previous.getWrappedComponent().getAttributes().get("disabled"))));
    }

    @Test
    public void shouldDisplaySearchForm() throws Exception {
        FacesPage homePage = tester.requestPage(HOME_PAGE);
        FacesComponent searchForm = homePage.getComponentWithId("search");
        assertTrue(searchForm.isRendered());
        FacesComponent queryInput = homePage.getComponentWithId("search:q");
        assertEquals(queryInput.getWrappedComponent().getClientId(), "q", "The prependId attribute on form should be false and the parent id not prepended to the child id");
        assertEquals(queryInput.getValueAsString(), null);
        FacesComponent searchButton = homePage.getComponentWithId("search:s");
        assertEquals(searchButton.getWrappedComponent().getClientId(), "s", "The prependId attribute on form should be false and the parent id not prepended to the child id");
        assertTrue(searchButton.getWrappedComponent() instanceof UICommand);
        assertEquals(((UICommand) searchButton.getWrappedComponent()).getActionExpression().getExpressionString(), "#{blog.search}");
    }
}
