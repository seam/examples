package org.jboss.seam.excel.test;

import java.io.ByteArrayInputStream;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.document.ByteArrayDocumentData;
import org.jboss.seam.document.DocumentData;
import org.jboss.seam.excel.ExcelTest.Person;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

/**
 * @author Daniel Roth
 * 
 *         Really small test. Renders an jxl excel file from jsf tags and
 *         verifies the content.
 * 
 */
public class RenderTest extends SeamTest {

    @Test
    public void testSimple() throws Exception {

        new FacesRequest() {

            @Override
            protected void updateModelValues() throws Exception {
            }

            @Override
            protected void invokeApplication() throws Exception {

                Renderer.instance().render("/simple.xhtml");

                DocumentData data = (DocumentData) Contexts.getEventContext().get("testExport");
                Workbook workbook = Workbook.getWorkbook(new ByteArrayInputStream(((ByteArrayDocumentData)data).getData()));
                Sheet sheet = workbook.getSheet("Developers");
                
                assert sheet != null;

                assert "Daniel Roth".equals(sheet.getCell(0, 0).getContents());
                assert "Nicklas Karlsson".equals(sheet.getCell(0, 1).getContents());

            }
        }.run();
    }

    public static void main(String[] args) {
        RenderTest t = new RenderTest();
        try {
            t.testSimple();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
