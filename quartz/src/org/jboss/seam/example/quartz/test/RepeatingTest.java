package org.jboss.seam.example.quartz.test;

import static org.jboss.seam.example.quartz.Payment.Frequency.EVERY_SECOND;

import java.math.BigDecimal;

import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.example.quartz.Account;
import org.jboss.seam.example.quartz.Payment;
import org.jboss.seam.example.quartz.Payment.Frequency;
import org.jboss.seam.mock.DBUnitSeamTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Pete Muir
 *
 */

public class RepeatingTest 
    extends DBUnitSeamTest 
{
    private QuartzTriggerHandle quartzTriggerHandle;
    private Long paymentId;
    
    private static final Frequency REPEATING = EVERY_SECOND;

    
    @Override
    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/example/quartz/test/BaseData.xml")
        );
    }
    
    
    @Test
    public void scheduleRepeating() throws Exception
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
                    setValue("#{newPayment.amount}", new BigDecimal("110.00"));
                    setValue("#{newPayment.paymentFrequency}", REPEATING );
                }
                
                @Override
                protected void invokeApplication() throws Exception 
                {
                    // schedule a the repeating job and immediately pause it
                    // this allows you to carefully control how long the timer
                    // runs for
                    assert "persisted".equals(invokeMethod("#{paymentHome.saveAndSchedule}"));
                    Payment payment = (Payment) getValue("#{newPayment}");
                    quartzTriggerHandle = payment.getQuartzTriggerHandle();
                    quartzTriggerHandle.pause();
                }
    
                @Override
                protected void renderResponse() throws Exception 
                {
                    // Check the job exists
                    assert ((Boolean)getValue("#{accountHome.idDefined}"));                
                    Account account = (Account) getValue("#{selectedAccount}");                
                    assert account !=null;
                    assert account.getId() == 1;
                    assert account.getPayments().size() == 1;            
                    
                    Payment payment = (Payment) getValue("#{newPayment}");
                    assert payment.getPayee().equals("IRS");
                    assert payment.getAmount().equals(new BigDecimal("110.00"));
                    assert payment.getAccount() != null;
                    assert payment.getAccount().getId() == 1;
                    assert payment.getActive();
                }
                
            }.run();
            
            // Start the triggerHandle, wait a lot shorter than the job takes
            // At this point the job should have executed once exactly
            quartzTriggerHandle.resume();
            pause((long) (REPEATING.getInterval() * 0.1));
            quartzTriggerHandle.pause();
            
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
                    assert new BigDecimal("110.00").equals(payment.getAmount());
                    assert payment.getActive();
                    assert REPEATING.equals(payment.getPaymentFrequency());
                    
                    assert new BigDecimal("891.46").equals(account.getBalance());
                }
                
            }.run();
            
            // Start the triggerHandle, wait until some short time after the 
            // job triggers once
            quartzTriggerHandle.resume();
            pause(REPEATING.getInterval());
            quartzTriggerHandle.pause();
            
            
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

                    // Job should have run twice
                    assert ((Boolean)getValue("#{accountHome.idDefined}"));                
                    Account account = (Account) getValue("#{selectedAccount}");

                    assert account !=null;
                    assert account.getId() == 1;
                    assert account.getPayments().size() == 1;
                    Payment payment = account.getPayments().get(0);
                    assert new BigDecimal("110.00").equals(payment.getAmount());
                    assert payment.getActive();
                    assert REPEATING.equals(payment.getPaymentFrequency());
                    assert payment.getLastPaid() != null;
                    assert new BigDecimal("781.46").equals(account.getBalance());
                    paymentId = payment.getId();
                }
                
            }.run();
            
            new FacesRequest("/search.xhtml", id)
            {
    
                @Override
                protected void beforeRequest() 
                {
                    setParameter("accountId", "1");
                    setParameter("paymentId", paymentId.toString());
                }
                
                @Override
                protected void invokeApplication() throws Exception
                {
                    // Resume the job, then cancel it
                    quartzTriggerHandle.resume();
                    invokeMethod("#{paymentHome.cancel}");
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
                    assert new BigDecimal("110.00").equals(payment.getAmount());
                    assert !payment.getActive();
                    assert payment.getLastPaid() != null;
                    assert new BigDecimal("781.46").equals(account.getBalance());
                }
                
            }.run();
            
            // Wait until some short time after another execution would
            // have occurred to check it really stops
            pause((long) (REPEATING.getInterval() * 1.05));
            
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
                    assert new BigDecimal("781.46").equals(account.getBalance());
                }
                
            }.run();
            
        }
        finally
        {
            if (quartzTriggerHandle != null)
            {
                quartzTriggerHandle.cancel();
            }
        }
    }
    
    
    
    @Test
    public void scheduleRepeatingWithStartAndEndTime() throws Exception
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
                    setValue("#{newPayment.amount}", new BigDecimal("110.00"));
                    setValue("#{newPayment.paymentFrequency}", REPEATING );
                }
                
                @Override
                protected void invokeApplication() throws Exception 
                {
                    // schedule a the repeating job and immediately pause it
                    // this allows you to carefully control how long the timer
                    // runs for
                    Payment payment = (Payment) getValue("#{newPayment}");
                    payment.setPaymentDate(new java.sql.Timestamp( System.currentTimeMillis() + REPEATING.getInterval()));
                    payment.setPaymentEndDate(new java.sql.Timestamp( (long) (System.currentTimeMillis() + (REPEATING.getInterval() * 2.5))));
                    assert "persisted".equals(invokeMethod("#{paymentHome.saveAndSchedule}")); 
                    quartzTriggerHandle = payment.getQuartzTriggerHandle();
                    quartzTriggerHandle.pause();
                }
    
                @Override
                protected void renderResponse() throws Exception 
                {
                    // Check the job exists
                    assert ((Boolean)getValue("#{accountHome.idDefined}"));                
                    Account account = (Account) getValue("#{selectedAccount}");                
                    assert account !=null;
                    assert account.getId() == 1;
                    assert account.getPayments().size() == 1;            
                    
                    Payment payment = (Payment) getValue("#{newPayment}");
                    assert payment.getPayee().equals("IRS");
                    assert payment.getAmount().equals(new BigDecimal("110.00"));
                    assert payment.getAccount() != null;
                    assert payment.getAccount().getId() == 1;
                    assert payment.getActive();
                }
                
            }.run();
            
            // Start the triggerHandle, wait a lot shorter than the job takes
            // At this point the job should have executed zero times
            quartzTriggerHandle.resume();
            pause((long) (REPEATING.getInterval() * 0.1));
            quartzTriggerHandle.pause();
            
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
                    assert new BigDecimal("110.00").equals(payment.getAmount());
                    assert payment.getActive();
                    assert new BigDecimal("1001.46").equals(account.getBalance());
                }
                
            }.run();
            
            // Start the triggerHandle, wait until some short time after the 
            // job triggers once
            quartzTriggerHandle.resume();
            pause(REPEATING.getInterval());
            quartzTriggerHandle.pause();
            
            
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

                    // Job should have run twice
                    assert ((Boolean)getValue("#{accountHome.idDefined}"));                
                    Account account = (Account) getValue("#{selectedAccount}");

                    assert account !=null;
                    assert account.getId() == 1;
                    assert account.getPayments().size() == 1;
                    Payment payment = account.getPayments().get(0);
                    assert new BigDecimal("110.00").equals(payment.getAmount());
                    assert payment.getActive();
                    assert new BigDecimal("891.46").equals(account.getBalance());
                    paymentId = payment.getId();
                }
                
            }.run();
            
            // Start the triggerHandle, wait until some short time after the 
            // job triggers once
            quartzTriggerHandle.resume();
            pause(REPEATING.getInterval());
            // Thats the last job so need to pause
            
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

                    // Job should have run twice
                    assert ((Boolean)getValue("#{accountHome.idDefined}"));                
                    Account account = (Account) getValue("#{selectedAccount}");

                    assert account !=null;
                    assert account.getId() == 1;
                    assert account.getPayments().size() == 1;
                    Payment payment = account.getPayments().get(0);
                    assert new BigDecimal("110.00").equals(payment.getAmount());
                    assert payment.getActive();
                    assert new BigDecimal("781.46").equals(account.getBalance());
                    paymentId = payment.getId();
                }
                
            }.run();
            
            // Start the triggerHandle, wait until some short time after the 
            // job would trigger again - we should be way beyond the expiry now 
            pause(REPEATING.getInterval());
            
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
                    assert new BigDecimal("110.00").equals(payment.getAmount());
                    assert payment.getActive();
                    assert payment.getLastPaid() != null;
                    assert new BigDecimal("781.46").equals(account.getBalance());
                }
                
            }.run();
            
        }
        finally
        {
            if (quartzTriggerHandle != null)
            {
                quartzTriggerHandle.cancel();
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
