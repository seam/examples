package org.jboss.seam.example.seamdiscs.test;

import static org.jboss.seam.example.seamdiscs.test.TestStrings.DISC1_ARTIST;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.DISC1_NAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.DISC2_ARTIST;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.DISC2_NAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.DISC3_ARTIST;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.DISC3_NAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.DISC4_ARTIST;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.DISC4_NAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.DISC5_ARTIST;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.DISC5_NAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.DISC6_ARTIST;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.DISC6_NAME;

import javax.faces.model.DataModel;

import org.jboss.seam.example.seamdiscs.model.Disc;
import org.jboss.seam.mock.DBUnitSeamTest;
import org.testng.annotations.Test;


public class DisplayDiscTest extends DBUnitSeamTest
{
    
    @Override
    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/example/seamdiscs/test/BaseData.xml")
        );
    }

    @Test
    public void testDisplayDiscs() throws Exception
    {
        new FacesRequest("/discs.xhtml")
        {
            
            @Override
            protected void renderResponse() throws Exception 
            {
                Object discs = getValue("#{discs.dataModel}");
                assert discs instanceof DataModel;
                DataModel dataModel = (DataModel) discs;
                
                // Check for the correct number of results
                assert dataModel.getRowCount() == 6;
                
                // Check for correct ordering
                assertDisc(dataModel, 0, DISC5_NAME, DISC5_ARTIST);
                assertDisc(dataModel, 1, DISC6_NAME, DISC6_ARTIST);
                assertDisc(dataModel, 2, DISC1_NAME, DISC1_ARTIST);
                assertDisc(dataModel, 3, DISC2_NAME, DISC2_ARTIST);
                assertDisc(dataModel, 4, DISC4_NAME, DISC4_ARTIST);
                assertDisc(dataModel, 5, DISC3_NAME, DISC3_ARTIST);
            }
            
        }.run();
    }
    
    @Test
    public void testDisplayDisc() throws Exception
    {
        // TODO Test navigation, but need a MockNavigationHandler
        new NonFacesRequest("/discs.xhtml")
        {
            @Override
            protected void beforeRequest() 
            {
                setParameter("actionOutcome", "disc");
                setParameter("discId", "3");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert ((Integer) 3).equals(getValue("#{discHome.id}"));
                Object object = null;
                object = getValue("#{disc}");
                assert object instanceof Disc;
                Disc disc1 = (Disc) object;
                assert DISC3_NAME.equals(disc1.getName());
            }
        }.run();
    }
    
    
    
    private void assertDisc(DataModel dataModel, int row, String discName, String artistName)
    {
        dataModel.setRowIndex(row);
        Object rowData = dataModel.getRowData();
        assert rowData instanceof Disc;
        Disc disc = (Disc) rowData;
        assert discName.equals(disc.getName());
        assert artistName.equals(disc.getArtist().getName());
    }
    
}
