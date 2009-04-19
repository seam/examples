/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */ 
package com.jboss.dvd.seam;

public interface Checkout
{
    public void createOrder();
    public Order submitOrder();
    
    public void destroy();
}
