/* 
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
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

