package org.jboss.seam.example.seamdiscs.test;

import static org.jboss.seam.example.seamdiscs.test.TestStrings.PASSWORD;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.USERNAME;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.WRONG_PASSWORD;
import static org.jboss.seam.example.seamdiscs.test.TestStrings.WRONG_USERNAME;

import org.jboss.seam.mock.DBUnitSeamTest;
import org.testng.annotations.Test;

/**
 * @author Pete Muir
 *
 */
public class LoginTest extends DBUnitSeamTest {
    
    @Override
    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/example/seamdiscs/test/BaseData.xml")
        );
    }
    
    @Test
    public void testLogin() throws Exception
    {
        new FacesRequest("/login.xhtml")
        {
            
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
            }
        }.run();
        
        new FacesRequest("/login.xhtml")
        {
            
            @Override
            protected void updateModelValues() throws Exception {
                setValue("#{identity.username}", USERNAME);
                setValue("#{identity.password}", WRONG_PASSWORD);
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                invokeAction("#{identity.login}");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert !((Boolean) getValue("#{identity.loggedIn}"));
            }
        }.run();
        
        new FacesRequest("/login.xhtml")
        {
            
            @Override
            protected void updateModelValues() throws Exception {
                setValue("#{identity.username}", WRONG_USERNAME);
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
                assert !((Boolean) getValue("#{identity.loggedIn}"));
            }
        }.run();
        
        new FacesRequest("/login.xhtml")
        {
            
            @Override
            protected void updateModelValues() throws Exception {
                setValue("#{identity.username}", WRONG_USERNAME);
                setValue("#{identity.password}", WRONG_PASSWORD);
            }
            
            @Override
            protected void invokeApplication() throws Exception 
            {
                invokeAction("#{identity.login}");
            }
            
            @Override
            protected void renderResponse() throws Exception 
            {
                assert !((Boolean) getValue("#{identity.loggedIn}"));
            }
        }.run();
    }

}
