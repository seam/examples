package org.jboss.seam.examples.booking.model;

import java.math.BigDecimal;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Hotel.class)
public abstract class Hotel_ {

    public static volatile SingularAttribute<Hotel, Long> id;
    public static volatile SingularAttribute<Hotel, String> zip;
    public static volatile SingularAttribute<Hotel, BigDecimal> price;
    public static volatile SingularAttribute<Hotel, String> address;
    public static volatile SingularAttribute<Hotel, Integer> stars;
    public static volatile SingularAttribute<Hotel, String> name;
    public static volatile SingularAttribute<Hotel, String> state;
    public static volatile SingularAttribute<Hotel, String> country;
    public static volatile SingularAttribute<Hotel, String> city;

}
