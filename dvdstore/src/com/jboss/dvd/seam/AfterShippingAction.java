package com.jboss.dvd.seam;

import java.math.BigDecimal;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

/**
 * An example of a Seam component used to handle a
 * jBPM transition event.
 * 
 * @author Gavin King
 */
@Name("afterShipping")
public class AfterShippingAction {
    @In Long orderId;
    @In BigDecimal amount;
    @In(scope=ScopeType.BUSINESS_PROCESS) 
    String customer;
    
    public void log()
    {
        System.out.println( "We shipped: " + orderId + " to: " + customer + ", amount: " + amount );
    }
}
