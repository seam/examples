package org.jboss.seam.example.quartz.test;

import java.util.List;

import org.jboss.seam.example.quartz.Account;
import org.jboss.seam.example.quartz.Payment;
import org.jboss.seam.mock.DBUnitSeamTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Pete Muir
 *
 */
public class AccountTest 
    extends DBUnitSeamTest 
{
    
    @Override
    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/example/quartz/test/BaseData.xml")
        );
    }
    
    @Test
    public void listAccounts() throws Exception 
    {
        new FacesRequest("/search.xhtml") 
        {
            
            @Override
            @SuppressWarnings("unchecked")
            protected void renderResponse() throws Exception 
            {
                List<Account> accounts = (List<Account>) getValue("#{accounts.resultList}");
                
                assert accounts.size() == 5;
            }
            
        }.run(); 
    }
    
    @Test
    public void selectAccount() throws Exception 
    {        
        String id = new FacesRequest("/search.xhtml") 
        {        
        
            @Override
            @SuppressWarnings("unchecked")
            protected void renderResponse() throws Exception 
            {
                assert !((Boolean)getValue("#{accountHome.idDefined}"));
            }          
        }.run();
        
        new FacesRequest("/search.xhtml", id) 
        {
            
            @Override
            protected void beforeRequest() 
            {
                setParameter("accountId", "1");
            }

            @Override
            protected void renderResponse() throws Exception 
            {
                assert ((Boolean) getValue("#{accountHome.idDefined}"));
                
                Account account = (Account) getValue("#{selectedAccount}");
                assert account !=null;
                assert account.getId() == 1;
                assert account.getPayments().size() == 0;
               
                Payment payment = (Payment) getValue("#{newPayment}");
                assert payment.getPayee().equals("Somebody");
                assert payment.getAccount() != null;
                assert payment.getAccount().getId() == 1;
                
            }            
        }.run();
        
        
        
    }
    
}
