/**
 * 
 */
package org.jboss.seam.example.seamdiscs.test;

import static org.jboss.seam.example.seamdiscs.test.TestStrings.ARTIST6_NAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.DISC4_ARTIST;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.DISC4_NAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.DISC4_NEW_DESCRIPTION;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.NEW_DISC_NAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.PASSWORD;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.USERNAME;

import javax.faces.model.DataModel;

import org.jboss.seam.example.seamdiscs.model.Artist;
import org.jboss.seam.example.seamdiscs.model.Disc;
import org.jboss.seam.mock.DBUnitSeamTest;
import org.testng.annotations.Test;

/**
 * @author Pete Muir
 *
 */
public class EditDiscTest extends DBUnitSeamTest{
    
    @Override
    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/example/seamdiscs/test/BaseData.xml")
        );
    }
    
    @Test
    public void testEditDisc() throws Exception
    {
        final String cid = new FacesRequest("/disc.xhtml")
        {
            @Override
            protected void beforeRequest() 
            {
                setParameter("discId", "4");
                setParameter("conversationPropagation", "join");
            }
            
            @Override
            protected void updateModelValues() throws Exception {
                setValue("#{identity.username}", USERNAME);
                setValue("#{identity.password}", PASSWORD);
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                invokeAction("#{identity.login}");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert (Boolean) getValue("#{identity.loggedIn}");
                assert ((Integer) 4).equals(getValue("#{discHome.id}"));
                assert DISC4_NAME.equals(getValue("#{disc.name}"));
                assert getValue("#{disc.description}") == null;
                assert isLongRunningConversation();
                assert (Boolean) getValue("#{discHome.managed}");
                
            }
        }.run();
        
        new FacesRequest("/disc.xhtml", cid)
        {      
            
            @Override
            protected void updateModelValues() throws Exception 
            {
                setValue("#{disc.description}", DISC4_NEW_DESCRIPTION);
                // Simulate the entity converter
                setValue("#{exampleArtist.name}", ARTIST6_NAME);
                Artist artist = (Artist) getValue("#{artists.singleResult}");
                setValue("#{disc.artist}", artist);
                assert isLongRunningConversation();
                assert cid.equals(getConversationId());
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                assert "updated".equals(invokeAction("#{discHome.update}"));
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert DISC4_NEW_DESCRIPTION.equals(getValue("#{disc.description"));
                assert DISC4_ARTIST.equals(getValue("#{disc.artist.name}"));
                assert isLongRunningConversation();
            }
            
        }.run();
    }
    
    @Test
    public void testAddDisc() throws Exception
    {
        final String cid = new FacesRequest("/disc.xhtml")
        {
            @Override
            protected void beforeRequest() 
            {
                setParameter("conversationPropagation", "join");
            }
            
            @Override
            protected void updateModelValues() throws Exception {
                setValue("#{identity.username}", USERNAME);
                setValue("#{identity.password}", PASSWORD);
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                invokeAction("#{identity.login}");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert (Boolean) getValue("#{identity.loggedIn}");
                assert null == getValue("#{discHome.id}");
                assert null == getValue("#{disc.name}");
                assert isLongRunningConversation();
                assert (!(Boolean) getValue("#{discHome.managed}"));
            }
        }.run();
        
        new FacesRequest("/disc.xhtml", cid)
        {      
            
            @Override
            protected void updateModelValues() throws Exception 
            {
                setValue("#{disc.name}", NEW_DISC_NAME);
                assert isLongRunningConversation();
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                assert "persisted".equals(invokeAction("#{discHome.persist}"));
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert NEW_DISC_NAME.equals(getValue("#{disc.name}"));
            }
            
        }.run();
        
        new FacesRequest("/discs.xhtml", cid)
        {
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert new Long("7").equals(getValue("#{discs.resultCount}"));
                assertDisc((DataModel) getValue("#{discs.dataModel}"), 4, NEW_DISC_NAME);
                assertDisc((DataModel) getValue("#{discs.dataModel}"), 5, DISC4_NAME);
            }
            
        }.run();        
    }
    
    private void assertDisc(DataModel dataModel, int row, String discName)
    {
        dataModel.setRowIndex(row);
        Object rowData = dataModel.getRowData();
        assert rowData instanceof Disc;
        Disc disc = (Disc) rowData;
        assert discName.equals(disc.getName());
    }
    
}
