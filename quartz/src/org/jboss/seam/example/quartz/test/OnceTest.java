package org.jboss.seam.example.quartz.test;

import static org.jboss.seam.example.quartz.Payment.Frequency.ONCE;

import java.math.BigDecimal;

import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.example.quartz.Account;
import org.jboss.seam.example.quartz.Payment;
import org.jboss.seam.mock.DBUnitSeamTest;
import org.testng.annotations.Test;
/**
 * 
 * @author Pete Muir
 *
 */

public class OnceTest extends DBUnitSeamTest 
{
    private QuartzTriggerHandle quartzTriggerHandle;
    
    @Override
    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/example/quartz/test/BaseData.xml")
        );
    }
    
   
    
    //@Test
    public void scheduleOnce() throws Exception
    {
        try
        {
            String id = new FacesRequest("/search.xhtml") 
            {
                @Override
                protected void beforeRequest() 
                {
                    setParameter("accountId", "1");
                }
                
                @Override
                protected void updateModelValues() throws Exception 
                {
                    setValue("#{newPayment.payee}", "IRS"); 
                    setValue("#{newPayment.amount}", new BigDecimal("100.00"));
                    setValue("#{newPayment.paymentFrequency}", ONCE);
                }
    
                @Override
                protected void invokeApplication() throws Exception 
                {
                    assert "persisted".equals(invokeMethod("#{paymentHome.saveAndSchedule}"));
                }
    
                @Override
                protected void renderResponse() throws Exception 
                {
                    assert ((Boolean)getValue("#{accountHome.idDefined}"));                
                    Account account = (Account) getValue("#{selectedAccount}");                
                    assert account !=null;
                    assert account.getId() == 1;
                    assert account.getPayments().size() == 1;               
                    
                    Payment payment = (Payment) getValue("#{newPayment}");
                    assert payment.getPayee().equals("IRS");
                    assert payment.getAmount().equals(new BigDecimal("100.00"));
                    assert payment.getAccount() != null;
                    assert payment.getAccount().getId() == 1;
                    quartzTriggerHandle = payment.getQuartzTriggerHandle();
                }
                
            }.run();
            
            // Wait, let quartz execute the job (async but straight away)
            pause(500);
            
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
                    assert ((Boolean)getValue("#{accountHome.idDefined}"));                
                    Account account = (Account) getValue("#{selectedAccount}");                
                    assert account !=null;
                    assert account.getId() == 1;
                    assert account.getPayments().size() == 1;
                    Payment payment = account.getPayments().get(0);
                    assert new BigDecimal("100.00").equals(payment.getAmount());
                    assert !payment.getActive();
                    assert ONCE.equals(payment.getPaymentFrequency());
                    assert payment.getLastPaid() != null;
                    assert new BigDecimal("901.46").equals(account.getBalance());
                }
                
            }.run();
        }
        finally
        {
            // Always cancel the job
            if (quartzTriggerHandle != null)
            {
                quartzTriggerHandle.cancel();
                quartzTriggerHandle = null;
            }
        }
    }
    
    private void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            
        }                
    }

    
    
}
