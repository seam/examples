//$Id$
package org.jboss.seam.example.spring.test;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;
import org.jboss.seam.example.spring.Booking;
import org.jboss.seam.example.spring.HibernateTestService;
import org.jboss.seam.example.spring.Hotel;
import org.jboss.seam.example.spring.HotelBookingAction;
import org.jboss.seam.example.spring.User;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class StartupTest extends SeamTest {

	@Test
	public void testRegister() throws Exception {
		new FacesRequest("/register.xhtml") {
			/**
			 * @see org.jboss.seam.mock.AbstractSeamTest.Request#updateModelValues()
			 */
			@Override
			protected void updateModelValues() throws Exception {
				setValue("#{user.username}", "testUser");
				setValue("#{user.name}", "Test User's Name");
				setValue("#{user.password}", "password");
				setValue("#{register.verify}", "password");
			}
			/**
			 * @see org.jboss.seam.mock.AbstractSeamTest.Request#invokeApplication()
			 */
			@Override
			protected void invokeApplication() throws Exception {
				invokeAction("#{register.register}");
			}
		}.run();
	}
	
	@Test(dependsOnMethods="testRegister")
	public void testBooking() throws Exception {
		new FacesRequest() {

			@Override
			protected void invokeApplication() throws Exception {
				Contexts.getSessionContext().set("user", new User("Gavin King", "foobar", "gavin"));
				setValue("#{identity.username}", "gavin");
				setValue("#{identity.password}", "foobar");
				invokeAction("#{identity.login}");
			}

		}.run();

		new FacesRequest("/main.xhtml") {

			@Override
			protected void invokeApplication() {
				assert invokeAction("#{hotelSearch.find}") == null;
			}

			@Override
			protected void renderResponse() {
				DataModel hotels = (DataModel) getValue("#{hotels}");
				hotels.setRowIndex(0);
				assert ((Hotel) hotels.getRowData()).getName().equals(HibernateTestService.HIBERNATE_HOTEL_NAME);
			}

		}.run();

		String id = new FacesRequest("/main.xhtml") {

			@Override
			protected void invokeApplication() throws Exception {
				HotelBookingAction hotelBooking = (HotelBookingAction) getInstance("hotelBooking");
				DataModel hotels = (DataModel) Contexts.getSessionContext().get("hotels");
				hotels.setRowIndex(0);
				hotelBooking.selectHotel((Hotel) hotels.getRowData());
			}

		}.run();

		id = new FacesRequest("/hotel.xhtml", id) {

			@Override
			protected void invokeApplication() {
				invokeAction("#{hotelBooking.bookHotel}");
			}

			@Override
			protected void renderResponse() {
				assert getValue("#{booking.user}") != null;
				assert getValue("#{booking.hotel}") != null;
				assert getValue("#{booking.creditCard}") == null;
				assert getValue("#{booking.creditCardName}") == null;
				Booking booking = (Booking) Contexts.getConversationContext().get("booking");
				assert booking.getHotel() == Contexts.getConversationContext().get("hotel");
				assert booking.getUser() == Contexts.getSessionContext().get("user");
				assert Manager.instance().isLongRunningConversation();
			}

		}.run();

		new FacesRequest("/book.xhtml", id) {

			@Override
			@SuppressWarnings("deprecation")
			protected void updateModelValues() throws Exception {
				setValue("#{booking.creditCard}", "1234567891021234");
				setValue("#{booking.creditCardName}", "GAVIN KING");
				setValue("#{booking.beds}", 2);
				Date now = new Date();
				setValue("#{booking.checkinDate}", now);
				setValue("#{booking.checkoutDate}", now);
			}

			@Override
			protected void invokeApplication() {
				assert invokeAction("#{hotelBooking.setBookingDetails}") == null;
			}

			@Override
			protected void renderResponse() {
				Iterator messages = FacesContext.getCurrentInstance().getMessages();
				assert messages.hasNext();
				FacesMessage message = (FacesMessage) messages.next();
				assert message.getSummary().equals("Check out date must be later than check in date");
				assert !messages.hasNext();
				assert Manager.instance().isLongRunningConversation();
			}

			@Override
			protected void afterRequest() {
				assert isInvokeApplicationComplete();
			}

		}.run();

		new FacesRequest("/book.xhtml", id) {

			@Override
			@SuppressWarnings("deprecation")
			protected void updateModelValues() throws Exception {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_MONTH, 2);
				setValue("#{booking.checkoutDate}", cal.getTime());
			}

			@Override
			protected void invokeApplication() {
				invokeAction("#{hotelBooking.setBookingDetails}");
			}

			@Override
			protected void renderResponse() {
				assert Manager.instance().isLongRunningConversation();
			}

			@Override
			protected void afterRequest() {
				assert isInvokeApplicationComplete();
			}

		}.run();

		new FacesRequest("/confirm.xhtml", id) {

			@Override
			protected void invokeApplication() {
				invokeAction("#{hotelBooking.confirm}");
			}

			@Override
			protected void afterRequest() {
				assert isInvokeApplicationComplete();
			}

		}.run();

		new NonFacesRequest("/main.xhtml") {

			@Override
			protected void renderResponse() {
				ListDataModel bookings = (ListDataModel) getInstance("bookings");
				assert bookings.getRowCount() == 1;
				bookings.setRowIndex(0);
				Booking booking = (Booking) bookings.getRowData();
				assert booking.getHotel().getName().equals(HibernateTestService.HIBERNATE_HOTEL_NAME);
				assert booking.getUser().getUsername().equals("gavin");
			}

		}.run();
	}
}
