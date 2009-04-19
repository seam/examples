package org.jboss.seam.example.seampay.test;

import java.math.BigDecimal;
import java.util.Date;

import org.jboss.seam.example.seampay.Account;
import org.jboss.seam.example.seampay.Payment;
import org.jboss.seam.example.seampay.PaymentProcessor;
import org.jboss.seam.example.seampay.Payment.Frequency;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class PaymentProcessorTest 
    extends SeamTest
{
    static final String     ACCOUNT_NUMBER = "X12345";
    static final BigDecimal INITIAL_BALANCE = new BigDecimal("1000");

    @Test 
    public void testInactive() {
        PaymentProcessor processor = new PaymentProcessor();
        
        Payment payment = createTestPayment(new BigDecimal("100"), Frequency.ONCE);
        payment.setActive(false);                
                
        assert payment.getAccount().getBalance().equals(INITIAL_BALANCE);        
               
        processor.processPayment(payment);
        
        assert payment.getAccount().getBalance().equals(INITIAL_BALANCE);
        assert payment.getLastPaid() == null;
    }
    
    @Test 
    public void testPayOnce() {
        PaymentProcessor processor = new PaymentProcessor();
        
        Payment payment = createTestPayment(new BigDecimal("100"), Frequency.ONCE);

        assert payment.getAccount().getBalance().equals(INITIAL_BALANCE); 

        processor.processPayment(payment);
               
        assert payment.getAccount().getBalance().equals(new BigDecimal("900"));
        assert !payment.getActive();
        assert payment.getLastPaid() != null;
    }
    
    @Test 
    public void testPayMultiple() {
        PaymentProcessor processor = new PaymentProcessor();
        
        Payment payment = createTestPayment(new BigDecimal("100"), Frequency.WEEKLY);

        assert payment.getAccount().getBalance().equals(INITIAL_BALANCE); 

        processor.processPayment(payment);
               
        assert payment.getAccount().getBalance().equals(new BigDecimal("900"));
        assert payment.getActive();
        assert payment.getLastPaid() != null;
        
        Date firstPayment = payment.getLastPaid();
        
        pause(); // just need to make sure we are some small time in the future
        
        processor.processPayment(payment);
     
        assert payment.getAccount().getBalance().equals(new BigDecimal("800"));
        assert payment.getActive();
        assert payment.getLastPaid().after(firstPayment);
    }
  
    
    private void pause() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            
        }                
    }

    protected Payment createTestPayment(BigDecimal amount, Frequency frequency) {
        Account account = new Account();
        account.setAccountNumber(ACCOUNT_NUMBER);
        setField(account, "balance", INITIAL_BALANCE);
        
        Payment payment = new Payment();
        payment.setAccount(account);
        payment.setAmount(amount);
        payment.setPaymentFrequency(frequency);        
        
        return payment;
    }
    
    
}
