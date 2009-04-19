//$Id$
package org.jboss.seam.example.spring;

import java.util.Calendar;
import javax.faces.application.FacesMessage;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

@Name("hotelBooking")
public class HotelBookingAction {
    @In("#{bookingService}")
    private BookingService bookingService;

    @In
    private User user;

    @In(required = false)
    @Out
    private Hotel hotel;

    @In(required = false)
    @Out(required = false)
    private Booking booking;

    @In
    private FacesMessages facesMessages;

    @In
    private Events events;

    @Logger
    private Log log;

    private boolean bookingValid;

    @Begin
    public void selectHotel(Hotel selectedHotel) {
        hotel = bookingService.findHotelById(selectedHotel.getId());
    }

    public void bookHotel() {
        booking = new Booking(hotel, user);
        Calendar calendar = Calendar.getInstance();
        booking.setCheckinDate(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        booking.setCheckoutDate(calendar.getTime());
    }

    public void setBookingDetails() {
        bookingValid = true;
        try {
            bookingService.validateBooking(booking);
        } catch (ValidationException e) {
            facesMessages.add(FacesMessage.SEVERITY_ERROR, e.getMessage());
            bookingValid = false;
        }
    }

    public boolean isBookingValid() {
        return bookingValid;
    }

    @End
    public String confirm() {
        try {
            bookingService.bookHotel(booking);
        } catch (ValidationException e) {
            facesMessages.add(FacesMessage.SEVERITY_ERROR, e.getMessage());
            return null;
        }
        
        BookingService.currentThread.set(true);
        //Cannot call this.sendRegisterEmail because it won't hit the @Asynchronous intercepter
        bookingService.sendRegisterEmail(booking.getUser().getUsername());
        BookingService.currentThread.set(null);

        facesMessages.add("Thank you, #{user.name}, your confimation number for #{hotel.name} is #{booking.id}");
        log.info("New booking: #{booking.id} for #{user.username}");

        events.raiseEvent("bookingConfirmed");
        return "main";
    }

    @End
    public void cancel() {
    }

}
