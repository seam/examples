package org.jboss.seam.examples.booking.model;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Booking.class)
public abstract class Booking_ {

    public static volatile SingularAttribute<Booking, Long> id;
    public static volatile SingularAttribute<Booking, Integer> beds;
    public static volatile SingularAttribute<Booking, Boolean> smoking;
    public static volatile SingularAttribute<Booking, String> creditCardName;
    public static volatile SingularAttribute<Booking, String> creditCardNumber;
    public static volatile SingularAttribute<Booking, Hotel> hotel;
    public static volatile SingularAttribute<Booking, Date> checkinDate;
    public static volatile SingularAttribute<Booking, Integer> creditCardExpiryYear;
    public static volatile SingularAttribute<Booking, Integer> creditCardExpiryMonth;
    public static volatile SingularAttribute<Booking, Date> checkoutDate;
    public static volatile SingularAttribute<Booking, User> user;
    public static volatile SingularAttribute<Booking, CreditCardType> creditCardType;

}
