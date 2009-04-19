/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.bpm.CreateProcess;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;

@Stateful
@Name("checkout")
public class CheckoutAction
    implements Checkout,
               Serializable
{
    private static final long serialVersionUID = -4651884454184474207L;

    @PersistenceContext(type=PersistenceContextType.EXTENDED)
    EntityManager em;

    @In(value="currentUser",required=false)
    Customer customer;

    @In(create=true)
    ShoppingCart cart;

    @Out(scope=ScopeType.CONVERSATION,required=false)
    Order currentOrder;
    @Out(scope=ScopeType.CONVERSATION,required=false)
    Order completedOrder;

    @Out(scope=ScopeType.BUSINESS_PROCESS, required=false)
    long orderId;
    @Out(scope=ScopeType.BUSINESS_PROCESS, required=false)
    BigDecimal amount = BigDecimal.ZERO;
    @Out(value="customer",scope=ScopeType.BUSINESS_PROCESS, required=false)
    String customerName;    

    
    @Begin(nested=true, pageflow="checkout")
    public void createOrder() {
        currentOrder = new Order();

        for (OrderLine line: cart.getCart()) {
            currentOrder.addProduct(em.find(Product.class, line.getProduct().getProductId()),
                                    line.getQuantity());
        }

        currentOrder.calculateTotals();
        cart.resetCart();       
    }

    @End
    @CreateProcess(definition="OrderManagement", processKey="#{completedOrder.orderId}")
    @Restrict("#{identity.loggedIn}")
    public Order submitOrder() {
        try {
            completedOrder = purchase(customer, currentOrder);
            
            orderId      = completedOrder.getOrderId();
            amount       = completedOrder.getNetAmount();
            customerName = completedOrder.getCustomer().getUserName();
    
        } catch (InsufficientQuantityException e) {
            for (Product product: e.getProducts()) {
                Contexts.getEventContext().set("prod", product);
                FacesMessages.instance().addFromResourceBundle("checkoutInsufficientQuantity");
            }
            return null;
        }

        return completedOrder;
    }

    private Order purchase(Customer customer, Order order) 
        throws InsufficientQuantityException
    {
        order.setCustomer(customer);
        order.setOrderDate(new Date());

        List<Product> errorProducts = new ArrayList<Product>();
        for (OrderLine line: order.getOrderLines()) {
            Inventory inv = line.getProduct().getInventory();
            if (!inv.order(line.getQuantity())) {
                errorProducts.add(line.getProduct());
            }
        }

        if (errorProducts.size()>0) {
            throw new InsufficientQuantityException(errorProducts);
        }

        order.calculateTotals();
        em.persist(order);

        return order;
    }

    @Remove
    public void destroy() {}
    
}
