package org.jboss.seam.example.quartz.test;

import static org.jboss.seam.annotations.Install.MOCK;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.example.quartz.Payment;
import org.jboss.seam.example.quartz.PaymentProcessor;
import org.jboss.seam.log.Log;

/**
 * @author Pete Muir
 *
 */
@Name("processor")
@Install(precedence=MOCK)
@AutoCreate
public class TestPaymentProcessor extends PaymentProcessor
{
    
    @In 
    EntityManager entityManager;

    @Logger Log log;
    
    @Asynchronous
    @Transactional
    public QuartzTriggerHandle schedulePaymentAsynchronously(Payment payment) 
    { 
        payment = entityManager.merge(payment);
        
        log.info("[#0] Processing cron payment #1", System.currentTimeMillis(), payment.getId());

        if (payment.getActive()) {
            BigDecimal balance = payment.getAccount().adjustBalance(payment.getAmount().negate());
            log.info(":: balance is now #0", balance);
            payment.setLastPaid(new Date());

        }

        return null;
    }
    
    @Observer("org.jboss.seam.example.quartz.test.scheduleAndSave")
    @Transactional
    public QuartzTriggerHandle schedulePayment(Payment payment) 
    { 
        payment = entityManager.merge(payment);
        
        log.error("[#0] Processing cron payment #1", System.currentTimeMillis(), payment.getId());
        if (payment.getActive()) {
            BigDecimal balance = payment.getAccount().adjustBalance(payment.getAmount().negate());
            log.error(":: balance is now #0", balance);
            payment.setLastPaid(new Date());

        }

        return null;
    }
    
    @Observer("org.jboss.seam.example.quartz.test.transactionSuccess")
    @Transactional
    public void observeTransactionSuccess(Payment payment)
    {
        TransactionStatus.instance().setTransactionSucceded(true);
        TransactionStatus.instance().setId(payment.getId());
    }
    
    @Observer("org.jboss.seam.example.quartz.test.transactionCompletion")
    @Transactional
    public void observeTransactionCompletion(Payment payment)
    {
        TransactionStatus.instance().setTransactionCompleted(true);
        TransactionStatus.instance().setId(payment.getId());
    }

}
