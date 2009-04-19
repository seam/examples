//$Id$
package org.jboss.seam.example.spring;

import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

@Scope(SESSION)
@Name("bookingList")
public class BookingListAction
    implements Serializable
{
    private static final long serialVersionUID = 8037511081807516466L;

    @In
    private BookingService bookingService;

    @In
    private User user;

    @SuppressWarnings("unused")
    @DataModel
    private List<Booking> bookings;
    @DataModelSelection
    private Booking booking;

    @Logger
    private Log log;

    @Factory("bookings")
    @Observer("bookingConfirmed")
    public void getBookings()
    {
        bookings = bookingService.findBookingsByUsername(user.getUsername());
    }

    public void cancel()
    {
        log.info("Cancel booking: #{bookingList.booking.id} for #{user.username}");
        bookingService.cancelBooking(booking.getId());
        getBookings();
        FacesMessages.instance().add("Booking cancelled for confirmation number #0", booking.getId());
    }

    public Booking getBooking()
    {
        return booking;
    }

}
