package org.jboss.seam.example.pdf;

import org.jboss.seam.annotations.*;

import java.math.BigDecimal;
import java.util.Date;

@Name("currentOrder")
public class OrderInfo {
    @Unwrap
    public Order getOrder() {
        Order order = new Order();
        
        order.setOrderDate(new Date());
        order.setCustomerName("Seamus Finnigan");

        order.setOrderId("ZT193881");

        order.setBaseAmount(new BigDecimal("199.99"));
        order.setTax(new BigDecimal("16.50"));
        order.setTotalAmount(order.getBaseAmount().add(order.getTax()));
        
        return order;
    }


    public static class Order {
        Date orderDate;

        String     customerName;
        String     orderId;
        BigDecimal baseAmount;
        BigDecimal tax;
        BigDecimal totalAmount;



        public BigDecimal getBaseAmount() {
            return baseAmount;
        }
        public void setBaseAmount(BigDecimal baseAmount) {
            this.baseAmount = baseAmount;
        }
        public String getCustomerName() {
            return customerName;
        }
        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }
        public String getOrderId() {
            return orderId;
        }
        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }
        public Date getOrderDate() {
            return orderDate;
        }
        public void setOrderDate(Date orderDate) {
            this.orderDate = orderDate;
        }
        public BigDecimal getTax() {
            return tax;
        }
        public void setTax(BigDecimal tax) {
            this.tax = tax;
        }
        public BigDecimal getTotalAmount() {
            return totalAmount;
        }
        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }        
    }

}
