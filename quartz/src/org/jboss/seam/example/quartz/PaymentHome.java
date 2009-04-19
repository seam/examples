package org.jboss.seam.example.quartz;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.example.quartz.Payment;
import org.jboss.seam.example.quartz.PaymentProcessor;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.log.Log;

@Name("paymentHome")
public class PaymentHome
    extends EntityHome<Payment>
{
    @RequestParameter Long paymentId;
    @In PaymentProcessor processor;
    
    @Logger Log log;

    public String saveAndSchedule()
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

    public String saveAndScheduleCron()
    {
        String result = persist();
        
        Payment payment = getInstance();
        log.info("scheduling instance #0", payment);

        QuartzTriggerHandle handle = processor.schedulePayment(payment.getPaymentDate(), 
                                                payment.getPaymentCron(), 
                                                payment.getPaymentEndDate(), 
                                                payment);
        
        payment.setQuartzTriggerHandle( handle );

        return result;
    }

    @Override
    public Object getId() {
        return paymentId;
    }

    @Transactional
    public void cancel() {
        Payment payment = getInstance();
        
        QuartzTriggerHandle handle = payment.getQuartzTriggerHandle();
        payment.setQuartzTriggerHandle(null);
        payment.setActive(false);
        
        try
        {
            handle.cancel();
        }
        catch (Exception nsole)
        {
            FacesMessages.instance().add("Payment already processed");
        }
    }
    
}
