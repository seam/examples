package org.jboss.seam.example.seamdiscs.test;

import static org.jboss.seam.example.seamdiscs.test.TestStrings.ARTIST1_NAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.ARTIST2_NAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.ARTIST3_NAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.ARTIST4_NAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.ARTIST5_NAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.ARTIST6_NAME;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;

import org.apache.myfaces.trinidad.model.CollectionModel;
import org.apache.myfaces.trinidad.model.SortCriterion;
import org.jboss.seam.example.seamdiscs.model.Artist;
import org.jboss.seam.example.seamdiscs.model.Band;
import org.jboss.seam.mock.DBUnitSeamTest;
import org.testng.annotations.Test;


public class DisplayArtistTest extends DBUnitSeamTest
{
    
    @Override
    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/example/seamdiscs/test/BaseData.xml")
        );
    }

    @Test
    public void testDisplayArtists() throws Exception
    {
        new NonFacesRequest("/artists.xhtml")
        {
            
            @Override
            protected void renderResponse() throws Exception 
            {
                Object artists = getValue("#{artists.dataModel}");
                assert artists instanceof DataModel;
                DataModel artistsDataModel = (DataModel) artists;
                
                // Check for the correct number of results
                assert artistsDataModel.getRowCount() == 6;
                
                // Check for correct ordering
                assertArtist(artistsDataModel, 0, ARTIST5_NAME);
                assertArtist(artistsDataModel, 1, ARTIST6_NAME);
                assertArtist(artistsDataModel, 2, ARTIST1_NAME);
                assertArtist(artistsDataModel, 3, ARTIST4_NAME);
                assertArtist(artistsDataModel, 4, ARTIST2_NAME);
                assertArtist(artistsDataModel, 5, ARTIST3_NAME);
            }
            
        }.run();
    }
    
    @Test
    public void testFilterArtists() throws Exception
    {
        new FacesRequest("/artists.xhtml")
        {
            
            @Override
            protected void updateModelValues() throws Exception 
            {
                setValue("#{exampleArtist.name}", "r");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                Object artists = getValue("#{artists.dataModel}");
                assert artists instanceof DataModel;
                DataModel artistsDataModel = (DataModel) artists;
                
                // Check for the correct number of results
                assert artistsDataModel.getRowCount() == 2;
                
                // Check for correct ordering
                assertArtist(artistsDataModel, 0, ARTIST5_NAME);
                assertArtist(artistsDataModel, 1, ARTIST6_NAME);
            }
            
        }.run();
        
        new FacesRequest("/artists.xhtml")
        {
            
            @Override
            protected void updateModelValues() throws Exception 
            {
                setValue("#{exampleArtist.name}", "Ri");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                Object artists = getValue("#{artists.dataModel}");
                assert artists instanceof DataModel;
                DataModel artistsDataModel = (DataModel) artists;
                
                // Check for the correct number of results
                assert artistsDataModel.getRowCount() == 1;
                
                // Check for correct ordering
                assertArtist(artistsDataModel, 0, ARTIST6_NAME);
            }
            
        }.run();
    }
    
    @Test
    public void testSeamCollectionModel() throws Exception
    {
        new NonFacesRequest("/artists.xhtml")
        {
            
            @Override
            protected void renderResponse() throws Exception 
            {
                Object artists = getValue("#{artists.dataModel}");
                assert artists instanceof CollectionModel;
                CollectionModel collectionModel = (CollectionModel) artists;
                
                // Reorder the list               
                List<SortCriterion> criteria = new ArrayList<SortCriterion>();
                criteria.add(new SortCriterion("artist.name", true));
                collectionModel.setSortCriteria(criteria);
                
                // Check for correct ordering
                assertArtist(collectionModel, 5, ARTIST5_NAME);
                assertArtist(collectionModel, 4, ARTIST6_NAME);
                assertArtist(collectionModel, 3, ARTIST1_NAME);
                assertArtist(collectionModel, 2, ARTIST4_NAME);
                assertArtist(collectionModel, 1, ARTIST2_NAME);
                assertArtist(collectionModel, 0, ARTIST3_NAME);
            }
            
        }.run();
    }
    
    @Test
    public void testDisplayArtist() throws Exception
    {
        // TODO Test navigation, but need a MockNavigationHandler
        new NonFacesRequest("/artists.xhtml")
        {
            @Override
            protected void beforeRequest() 
            {
                setParameter("actionOutcome", "artist");
                setParameter("artistId", "1");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert ((Integer) 1).equals(getValue("#{artistHome.id}"));
                Object object = null;
                object = getValue("#{artist}");
                assert object instanceof Band;
                Band artist1 = (Band) object;
                assert ARTIST1_NAME.equals(artist1.getName());
                assert artist1.getBandMembers().size() == 3;
            }
        }.run();
    }
    
    
    
    private void assertArtist(DataModel dataModel, int row, String name)
    {
        dataModel.setRowIndex(row);
        Object rowData = dataModel.getRowData();
        assert rowData instanceof Artist;
        Artist artist = (Artist) rowData;
        assert name.equals(artist.getName());
    }
    
}
