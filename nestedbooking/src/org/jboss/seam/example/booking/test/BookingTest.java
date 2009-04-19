//$Id$
package org.jboss.seam.example.booking.test;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;
import org.jboss.seam.example.booking.Booking;
import org.jboss.seam.example.booking.Hotel;
import org.jboss.seam.example.booking.HotelBooking;
import org.jboss.seam.example.booking.User;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class BookingTest extends SeamTest
{
   
   @Test
   public void testBookHotel() throws Exception
   {
      
      new FacesRequest() {
         
         @Override
         protected void invokeApplication()
         {
            Contexts.getSessionContext().set("user", new User("Gavin King", "foobar", "gavin"));
            setValue("#{identity.username}", "gavin");
            setValue("#{identity.password}", "foobar");            
            invokeMethod("#{identity.login}");
         }
         
      }.run();
      
      new FacesRequest("/main.xhtml") {

         @Override
         protected void updateModelValues()
         {
            setValue("#{hotelSearch.searchString}", "Union Square");
         }

         @Override
         protected void invokeApplication()
         {
            assert invokeMethod("#{hotelSearch.find}")==null;
         }

         @Override
         protected void renderResponse()
         {
            DataModel hotels = (DataModel) Contexts.getSessionContext().get("hotels");
            assert hotels.getRowCount()==1;
            assert ( (Hotel) hotels.getRowData() ).getCity().equals("NY");
            assert getValue("#{hotelSearch.searchString}").equals("Union Square");
            assert !Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      final String id = new FacesRequest("/main.xhtml") {
         
         @Override
         protected void invokeApplication() 
         {
            HotelBooking hotelBooking = (HotelBooking) getInstance("hotelBooking");
            DataModel hotels = (DataModel) Contexts.getSessionContext().get("hotels");
            assert hotels.getRowCount()==1;
            hotelBooking.selectHotel( (Hotel) hotels.getRowData() );
         }

         @Override
         protected void renderResponse()
         {
            Hotel hotel = (Hotel) Contexts.getConversationContext().get("hotel");
            assert hotel.getCity().equals("NY");
            assert hotel.getZip().equals("10011");
            assert Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new FacesRequest("/hotel.xhtml", id) {

         @Override
         protected void invokeApplication()
         {
            invokeMethod("#{hotelBooking.bookHotel}");
         }

         @Override
         protected void renderResponse()
         {
            assert getValue("#{booking.user}")!=null;
            assert getValue("#{booking.hotel}")!=null;
            assert getValue("#{booking.creditCard}")==null;
            assert getValue("#{booking.creditCardName}")==null;
            Booking booking = (Booking) Contexts.getConversationContext().get("booking");
            assert booking.getHotel()==Contexts.getConversationContext().get("hotel");
            assert booking.getUser()==Contexts.getSessionContext().get("user");
            assert Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
            
      new FacesRequest("/book.xhtml", id) {
         
         @Override @SuppressWarnings("deprecation")
         protected void updateModelValues()
         {  
            Date now = new Date();
            setValue("#{booking.checkinDate}", now);
            setValue("#{booking.checkoutDate}", now);
         }

         @Override
         protected void invokeApplication()
         {
            assert invokeMethod("#{hotelBooking.setBookingDates}")==null;
         }

         @Override
         protected void renderResponse()
         {
            Iterator messages = FacesContext.getCurrentInstance().getMessages();
            assert messages.hasNext();
            FacesMessage message = (FacesMessage) messages.next();
            assert message.getSummary().equals("Check out date must be later than check in date");
            assert !messages.hasNext();
            assert Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new FacesRequest("/book.xhtml", id) {
         
         @Override @SuppressWarnings("deprecation")
         protected void updateModelValues()
         {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 2);
            setValue("#{booking.checkoutDate}", cal.getTime() );
         }

         @Override
         protected void invokeApplication()
         {
            assert "rooms".equals(invokeMethod("#{hotelBooking.setBookingDates}"));
         }

         @Override
         protected void renderResponse()
         {
             assert Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new FacesRequest("/rooms.xhtml", id) 
      {

         @Override
         protected void renderResponse()
         {
            assert getValue("#{booking.user}")!=null;
            assert getValue("#{booking.hotel}")!=null;
            assert getValue("#{booking.checkinDate}")!=null;
            assert getValue("#{booking.checkoutDate}")!=null;
            assert getValue("#{booking.roomPreference}").equals(getValue("#{hotel.standardRoom}"));
            assert getValue("#{booking.creditCard}")==null;
            assert getValue("#{booking.creditCardName}")==null;
            
            assert getValue("#{availableRooms.rowCount}").equals(new Integer(2));
            DataModel availableRooms = (DataModel) getValue("#{availableRooms}");
            availableRooms.setRowIndex(0);
            assert "Cozy Room".equals(getValue("#{availableRooms.rowData.name}"));
            availableRooms.setRowIndex(1);
            assert "Spectacular Room".equals(getValue("#{availableRooms.rowData.name}"));
         }
         
      }.run();
      
      final String nestedId = new FacesRequest("/rooms.xhtml", id) 
      {
         
         @Override
         protected void applyRequestValues()
         {
            DataModel availableRooms = (DataModel) getValue("#{availableRooms}");
            availableRooms.setRowIndex(0);
         }

         @Override
         protected void invokeApplication()
         {
            assert getValue("#{booking.roomPreference}")!=null;
            assert "Cozy Room".equals(getValue("#{booking.roomPreference.name}"));
            assert "payment".equals(invokeAction("#{roomPreference.selectPreference}"));
            System.out.println("here");
         }
         
         @Override
         protected void renderResponse()
         {
            assert Manager.instance().isLongRunningConversation();
            assert Manager.instance().isNestedConversation();
         }
         
      }.run();
      
      System.out.println(id + "/" + nestedId);
      // Hmm, need this to move to the new, nested, conversation
      // TODO This is probably a bug in SeamTest, not sure where
      new NonFacesRequest("/payment.xhtml", nestedId) 
      {
         
         @Override
         protected void renderResponse()
         {
            System.out.println("here");
            assert Manager.instance().isLongRunningConversation();
            assert Manager.instance().isNestedConversation();
            
            assert getValue("#{booking.user}")!=null;
            assert getValue("#{booking.hotel}")!=null;
            assert getValue("#{booking.checkinDate}")!=null;
            assert getValue("#{booking.checkoutDate}")!=null;
            assert getValue("#{booking.roomPreference}")!=null;
            assert getValue("#{booking.creditCard}")==null;
            assert getValue("#{booking.creditCardName}")==null;
         }
         
      }.run();
      
      new FacesRequest("/payment.xhtml", nestedId) 
      {

         @Override
         protected void processValidations()
         {
            validateValue("#{booking.creditCard}", "123");
            assert isValidationFailure();
         }

         @Override
         protected void renderResponse()
         {
            Iterator messages = FacesContext.getCurrentInstance().getMessages();
            assert messages.hasNext();
            assert ( (FacesMessage) messages.next() ).getSummary().equals("Credit card number must 16 digits long");
            assert !messages.hasNext();
            assert Manager.instance().isLongRunningConversation();
            assert Manager.instance().isNestedConversation();
         }
         
      }.run();
      
      new FacesRequest("/payment.xhtml", nestedId) 
      {

         @Override
         protected void processValidations()
         {
            validateValue("#{booking.creditCardName}", "");
            assert isValidationFailure();
         }

         @Override
         protected void renderResponse()
         {
            Iterator messages = FacesContext.getCurrentInstance().getMessages();
            assert messages.hasNext();
            assert ( (FacesMessage) messages.next() ).getSummary().equals("Credit card name is required");
            assert !messages.hasNext();
            assert Manager.instance().isLongRunningConversation();
            assert Manager.instance().isNestedConversation();
         }
         
      }.run();

      new FacesRequest("/payment.xhtml", nestedId)
      {
         @Override
         protected void updateModelValues()
         {
            setValue("#{booking.creditCard}", "1234567891021234");
            setValue("#{booking.creditCardName}", "GAVIN KING");
         }
         
         @Override
         protected void invokeApplication()
         {
            assert "confirm".equals(invokeAction("#{roomPreference.requestConfirmation}"));
         }
         
         @Override
         protected void renderResponse()
         {
            assert Manager.instance().isLongRunningConversation();
            assert Manager.instance().isNestedConversation();
         }
         
      }.run();
      
      new FacesRequest("/confirm.xhtml", nestedId) {

         @Override
         protected void invokeApplication()
         {
            invokeMethod("#{hotelBooking.confirm}");
         }
         
      }.run();
      
      new NonFacesRequest("/main.xhtml") {

         @Override
         protected void renderResponse()
         {
            ListDataModel bookings = (ListDataModel) getInstance("bookings");
            assert bookings.getRowCount()==1;
            bookings.setRowIndex(0);
            Booking booking = (Booking) bookings.getRowData();
            assert booking.getHotel().getCity().equals("NY");
            assert booking.getUser().getUsername().equals("gavin");
            assert !Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new FacesRequest("/main.xhtml") {
         
         @Override
         protected void invokeApplication()
         {
            ListDataModel bookings = (ListDataModel) Contexts.getSessionContext().get("bookings");
            bookings.setRowIndex(0);
            invokeMethod("#{bookingList.cancel}");
         }

         @Override
         protected void renderResponse()
         {
            ListDataModel bookings = (ListDataModel) Contexts.getSessionContext().get("bookings");
            assert bookings.getRowCount()==0;
            assert !Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
   }
   
}
