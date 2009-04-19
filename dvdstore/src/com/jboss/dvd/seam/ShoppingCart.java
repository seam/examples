/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ShoppingCart
{
    public boolean getIsEmpty();

    public void addProduct(Product product, int quantity);
    public List<OrderLine> getCart();
    @SuppressWarnings("unchecked")
    public Map getCartSelection();

    public BigDecimal getSubtotal();
    public BigDecimal getTax();
    public BigDecimal getTotal();

    public void updateCart();
    public void resetCart();

    public void destroy();
}
