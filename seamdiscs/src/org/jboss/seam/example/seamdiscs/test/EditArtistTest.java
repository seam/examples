/**
 * 
 */
package org.jboss.seam.example.seamdiscs.test;

import static org.jboss.seam.example.seamdiscs.test.TestStrings.ARTIST1_NAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.ARTIST4_NAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.ARTIST_NEW_DESCRIPTION;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.BANDMEMBER3_VALUE;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.NEW_ARTIST_NAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.NEW_BANDMEMBER_NAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.NEW_BAND_NAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.PASSWORD;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.PERSISTED;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.UPDATED;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.USERNAME;

import java.util.List;

import javax.el.PropertyNotFoundException;

import org.jboss.seam.example.seamdiscs.model.BandMember;
import org.jboss.seam.mock.DBUnitSeamTest;
import org.testng.annotations.Test;

/**
 * @author Pete Muir
 *
 */
public class EditArtistTest extends DBUnitSeamTest
{
    
    

    @Override
    protected void prepareDBUnitOperations() 
    {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/example/seamdiscs/test/BaseData.xml")
        );
    }
    
    @Test
    public void testEditArtist() throws Exception
    {
        final String cid = new FacesRequest("/artist.xhtml")
        {
            @Override
            protected void beforeRequest() 
            {
                setParameter("artistId", "1");
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
                assert ((Integer) 1).equals(getValue("#{artistHome.id}"));
                assert ARTIST1_NAME.equals(getValue("#{artist.name}"));
                assert getValue("#{artist.description}") == null;
                assert isLongRunningConversation();
                assert (Boolean) getValue("#{artistHome.managed}");
                
            }
        }.run();
        
        new FacesRequest("/artist.xhtml", cid)
        {      
            
            @Override
            protected void updateModelValues() throws Exception 
            {
                setValue("#{artist.description}", ARTIST_NEW_DESCRIPTION);
                assert isLongRunningConversation();
                assert cid.equals(getConversationId());
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                assert UPDATED.equals(invokeAction("#{artistHome.update}"));
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert ARTIST_NEW_DESCRIPTION.equals(getValue("#{artist.description"));
                assert isLongRunningConversation();
            }
            
        }.run();
    }
    
    @Test
    public void testAddArtist() throws Exception
    {
        final String cid = new FacesRequest("/artist.xhtml")
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
                assert null == getValue("#{artistHome.id}");
                assert null == getValue("#{artist.name}");
                assert isLongRunningConversation();
                assert (!(Boolean) getValue("#{artistHome.managed}"));
            }
        }.run();
        
        new FacesRequest("/artist.xhtml", cid)
        {      
            
            @Override
            protected void updateModelValues() throws Exception 
            {
                setValue("#{artistHome.instance.name}", NEW_ARTIST_NAME);
                assert isLongRunningConversation();
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                assert PERSISTED.equals(invokeAction("#{artistHome.persist}"));
            }
            
        }.run();
        
        new FacesRequest("/artists.xhtml", cid)
        {
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert new Long("7").equals(getValue("#{artists.resultCount}"));
                assert NEW_ARTIST_NAME.equals(getValue("#{artists.resultList[3].name}"));
                assert ARTIST4_NAME.equals(getValue("#{artists.resultList[4].name}"));
            }
            
        }.run();        
    }

    @Test
    public void testAddBand() throws Exception
    {
        final String cid = new FacesRequest("/artist.xhtml")
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
                setValue("#{artistHome.type}", "band");
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
                assert null == getValue("#{artistHome.id}");
                assert null == getValue("#{artist.name}");
                try
                {
                    assert ((Integer) 0).equals(getValue("#{artist.bandMembers.size}"));
                }
                catch (PropertyNotFoundException e) 
                {
                    assert false;
                }
                assert isLongRunningConversation();
                assert (!(Boolean) getValue("#{artistHome.managed}"));
            }
        }.run();
        
        new FacesRequest("/artist.xhtml", cid)
        {      
            
            @Override
            protected void updateModelValues() throws Exception 
            {
                setValue("#{artistHome.instance.name}", NEW_BAND_NAME);
                assert isLongRunningConversation();
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                assert PERSISTED.equals(invokeAction("#{artistHome.persist}"));
                assert ((Integer) 0).equals(getValue("#{artistHome.instance.bandMembers.size}"));
            }
            
        }.run();
        
        new FacesRequest("/artists.xhtml", cid)
        {
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert new Long("7").equals(getValue("#{artists.resultCount}"));
                assert NEW_BAND_NAME.equals(getValue("#{artists.resultList[3].name}"));
                assert ARTIST4_NAME.equals(getValue("#{artists.resultList[4].name}"));
            }
            
        }.run();
    }
    
    @Test
    public void testAddBandMember() throws Exception
    {
        final String cid = new FacesRequest("/artist.xhtml")
        {
            @Override
            protected void beforeRequest() 
            {
                setParameter("artistId", "1");
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
                assert ARTIST1_NAME.equals(getValue("#{artist.name}"));
                assert ((Integer) 3).equals(getValue("#{artist.bandMembers.size}"));
            }
        }.run();
        
        new FacesRequest("/artist.xhtml", cid)
        {
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                assert null == invokeAction("#{artistHome.addBandMember}");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert ((Integer) 4).equals(getValue("#{artist.bandMembers.size}"));
            }
            
        }.run();
        
        new FacesRequest("/artist.xhtml", cid)
        {       
            
            @Override
            protected void updateModelValues() throws Exception 
            {
                setValue("#{artist.bandMembers[3].name}", NEW_BANDMEMBER_NAME);
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                assert "updated" == invokeAction("#{artistHome.update}");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert ((Integer) 4).equals(getValue("#{artist.size}"));
                assert NEW_BANDMEMBER_NAME.equals(getValue("#{artist.bandMembers[3].name}"));
            }
            
        }.run();

    }
    
    @Test
    public void testBandMemberFinder() throws Exception
    {
        new NonFacesRequest("/artist.xhtml")
        {
            
            @Override
            protected void renderResponse() throws Exception 
            {
                List<BandMember> bandMembers = (List<BandMember>) invokeMethod("#{bandMemberFinder.getBandMembers('R')}");
                assert bandMembers.size() == 1;
                assert BANDMEMBER3_VALUE.equals(bandMembers.get(0).getName());
            }
            
        }.run();
    }
}
