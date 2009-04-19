//$Id$
package org.jboss.seam.example.hibernate;

// import static javax.ejb.TransactionAttributeType.REQUIRES_NEW;
import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

@Scope(SESSION)
@Name("bookingList")
// @TransactionAttribute(REQUIRES_NEW)
public class BookingListAction implements Serializable
{
   
   @In
   private Session bookingDatabase;
   
   @In
   private User user;
   
   @DataModel
   private List<Booking> bookings;
   @DataModelSelection 
   private Booking booking;
   
   @Logger 
   private Log log;
   
   @Factory
   @Observer("bookingConfirmed")
   @Transactional
   public void getBookings()
   {
      bookings = bookingDatabase.createQuery("select b from Booking b where b.user.username = :username order by b.checkinDate")
            .setParameter("username", user.getUsername())
            .list();
   }
   
   public void cancel()
   {
      log.info("Cancel booking: #{bookingList.booking.id} for #{user.username}");
      Booking cancelled = (Booking) bookingDatabase.get(Booking.class, booking.getId());
      if (cancelled!=null) bookingDatabase.delete( cancelled );
      getBookings();
      FacesMessages.instance().add("Booking cancelled for confirmation number #0", booking.getId());
   }
   
   public Booking getBooking()
   {
      return booking;
   }
   
}
