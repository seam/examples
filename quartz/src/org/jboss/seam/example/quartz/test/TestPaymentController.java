package org.jboss.seam.example.quartz.test;

import static org.jboss.seam.annotations.Install.MOCK;

import java.sql.Timestamp;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.async.Schedule;
import org.jboss.seam.async.TimerSchedule;
import org.jboss.seam.core.Events;
import org.jboss.seam.example.quartz.Payment;
import org.jboss.seam.example.quartz.PaymentHome;
import org.jboss.seam.log.Log;

/**
 * @author Pete Muir
 *
 */
@Name("paymentHome")
@Install(precedence=MOCK)
public class TestPaymentController extends PaymentHome
{
    
    @In TestPaymentProcessor processor;
    
    @Logger Log log;
    
    public String scheduleAndSaveAsynchronously()
    {
        String result = persist();
        
        Payment payment = getInstance();
        
        log.info("scheduling instance #0", payment);
        QuartzTriggerHandle handle = processor.schedulePayment(payment.getPaymentDate(), 
                                                payment.getPaymentFrequency().getInterval(), 
                                                payment.getPaymentEndDate(), 
                                                payment);
        
        payment.setQuartzTriggerHandle( handle );

        return result;
    }
    
    public void scheduleAndSaveUsingAsynchronousEvent()
    {
        persist();
        Events.instance().raiseAsynchronousEvent("org.jboss.seam.example.quartz.test.scheduleAndSave", getInstance());
    }
    
    public void scheduleAndSaveUsingTimedEvent()
    {
        persist();
        // A simple once-only which exprires in 1000ms
        Schedule schedule = new TimerSchedule( new Timestamp( System.currentTimeMillis() + 1000l ) );
        Events.instance().raiseTimedEvent("org.jboss.seam.example.quartz.test.scheduleAndSave", schedule, getInstance());
    }
    
    public void scheduleAndSaveWithTransactionEvents()
    {
        TransactionStatus.clear();
        Events.instance().raiseTransactionSuccessEvent("org.jboss.seam.example.quartz.test.transactionSuccess", getInstance());
        Events.instance().raiseTransactionCompletionEvent("org.jboss.seam.example.quartz.test.transactionCompletion", getInstance());
        try
        {
            super.saveAndSchedule();
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public Class<Payment> getEntityClass()
    {
        return Payment.class;
    }
    
}
