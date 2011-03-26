package org.jboss.seam.examples.booking.ftest;

import org.jboss.test.selenium.locator.JQueryLocator;
import org.jboss.test.selenium.locator.option.OptionLocator;
import org.jboss.test.selenium.locator.option.OptionValueLocator;
import org.testng.annotations.Test;

import static org.jboss.test.selenium.guard.request.RequestTypeGuardFactory.waitXhr;
import static org.jboss.test.selenium.locator.LocatorFactory.jq;
import static org.testng.AssertJUnit.*;

/**
 * This class tests booking functionality of the example.
 * 
 * @author jbalunas
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 * 
 */
public class BookingTest extends AbstractBookingTest
{

   public static final JQueryLocator BUTTON_BOOK = jq("[id='actions:bookHotel']");
   public static final JQueryLocator BUTTON_PROCEED = jq("[id='bookingForm:proceed']");
   public static final JQueryLocator BUTTON_CONFIRM = jq("[id='confirmForm:confirm']");
   public static final JQueryLocator BUTTON_CANCEL = jq("[id='confirmForm:cancel']");
   public static final JQueryLocator BUTTON_REVISE = jq("[id='confirmForm:revise']");
   public static final String BOOKING_MESSAGE = "You're booked to stay at the";
   public static final String CANCEL_MESSAGE = "has been canceled.";

   public static final JQueryLocator COUNT_HOTEL = jq("[id='hotelSelectionForm:hotels'] tbody tr");
   public static final JQueryLocator COUNT_BOOKING = jq("[id='bookings:bookings'] tbody tr");
   
   public static final JQueryLocator BOOKING_TABLE_FIRST_ROW_NAME = jq("table[id='bookings:bookings'] tbody tr:first td:first");
   public static final JQueryLocator BOOKING_TABLE_FIRST_ROW_LINK = jq("[id='bookings:bookings:0:cancel']");
   public static final JQueryLocator BOOKING_CANCEL_MESSAGE = jq("[id='messages'] li");
   public static final String BOOKING_CANCEL_MESSAGE_TEXT = "Your booking at the .+? on .+? has been canceled\\.";

   public static final JQueryLocator DETAILS_CARD_TYPE = jq("[id='bookingForm:creditCardType:type']");
   public static final JQueryLocator DETAILS_CARD_NUMBER = jq("[id='bookingForm:creditCardNumber:input']");
   public static final JQueryLocator DETAILS_SMOKING = jq("[id='bookingForm:smokingPreference:input:0']");
   public static final JQueryLocator DETAILS_NONSMOKING = jq("[id='bookingForm:smokingPreference:input:1']");
   
   public static final JQueryLocator CONFIRM_TEXT = jq("[id='content']");
   
   public static final JQueryLocator SEARCH_PAGE_SIZE = jq("[id='pageSize']");

   /**
    * Tests the hotel search - with both existing and non-existing queries.
    */
   @Test
   public void testSearch()
   {
      enterSearchQuery("Marriott");
      assertFalse(selenium.isElementPresent(SEARCH_NO_RESULTS));
      assertEquals(2, selenium.getCount(COUNT_HOTEL));

      enterSearchQuery("nonExistingHotel");
      assertTrue(selenium.isElementPresent(SEARCH_NO_RESULTS));
      assertEquals(0, selenium.getCount(COUNT_HOTEL));
   }
   
   @Test
   public void testSearchPageSize()
   {
      int[] values = {5, 10, 20};
      
      selenium.type(SEARCH_QUERY, "a");
      
      for (int pageSize : values)
      {
         selenium.select(SEARCH_PAGE_SIZE, new OptionValueLocator(String.valueOf(pageSize)));
         waitXhr(selenium).keyUp(SEARCH_QUERY, " ");
         assertEquals(selenium.getCount(COUNT_HOTEL), pageSize);
      }
   }

   /**
    * Simply follows the booking wizard without changing anything.
    */
   @Test
   public void testSimpleBooking()
   {
      String hotelName = "Grand Hyatt";
      int bookingCount = selenium.getCount(COUNT_BOOKING);
      bookHotel(hotelName, CreditCardType.VISA);
      assertEquals(++bookingCount, selenium.getCount(COUNT_BOOKING));
   }
   
   /**
    * Tests "revise" and "cancel" buttons as well as that changed credit card details are propagated across the booking wizard.
    */
   @Test
   public void testMoreSophisticatedBooking()
   {
      String hotelName = "Conrad Miami";
      String creditCardNumber1 = "0123456789012347";
      CreditCardType creditCardType1 = CreditCardType.DISCOVER;
      String creditCardNumber2 = "6432109876543210";
      CreditCardType creditCardType2 = CreditCardType.AMEX;
      int bookingCount = selenium.getCount(COUNT_BOOKING);
      
      enterSearchQuery(hotelName);
      selenium.click(SEARCH_RESULT_TABLE_FIRST_ROW_LINK);
      selenium.waitForPageToLoad();
      // booking page
      selenium.click(BUTTON_BOOK);
      selenium.waitForPageToLoad();
      // booking detail page
      populateBookingFields(creditCardNumber1, creditCardType1);
      selenium.click(BUTTON_PROCEED);
      selenium.waitForPageToLoad();
      // confirmation page
      assertTrue(selenium.getText(CONFIRM_TEXT).contains(creditCardNumber1));
      assertTrue(selenium.getText(CONFIRM_TEXT).contains(creditCardType1.getName()));
      selenium.click(BUTTON_REVISE);
      selenium.waitForPageToLoad();
      // back to booking page
      populateBookingFields(creditCardNumber2, creditCardType2);
      selenium.click(BUTTON_PROCEED);
      selenium.waitForPageToLoad();
      // confirmation page
      assertTrue(selenium.getText(CONFIRM_TEXT).contains(creditCardNumber2));
      assertTrue(selenium.getText(CONFIRM_TEXT).contains(creditCardType2.getName()));
      // cancel booking
      selenium.click(BUTTON_CANCEL);
      selenium.waitForPageToLoad();
      // check that the booking cound remains unchanged
      assertEquals(bookingCount, selenium.getCount(COUNT_BOOKING));
   }
   
   @Test
   public void testBookingCanceling()
   {
      String[] hotelNames = new String[] { "Hilton Diagonal Mar", "Parc 55", "Ritz-Carlton Montreal", "Parc 55" };
      int bookingCount = selenium.getCount(COUNT_BOOKING);
      
      // make 3 bookings
      for (String hotelName : hotelNames)
      {
         bookHotel(hotelName, CreditCardType.VISA);
      }
      
      bookingCount += hotelNames.length;
      assertEquals(bookingCount, selenium.getCount(COUNT_BOOKING));
      
      for (int i = 0; i < hotelNames.length; i++)
      {
         String hotelName = selenium.getText(BOOKING_TABLE_FIRST_ROW_NAME).trim();
         selenium.click(BOOKING_TABLE_FIRST_ROW_LINK);
         selenium.waitForPageToLoad();
         String message = selenium.getText(BOOKING_CANCEL_MESSAGE);
         assertTrue("Unexpected canceling message: " + message, message.matches(BOOKING_CANCEL_MESSAGE_TEXT));
         assertTrue("Unexpected hotel name.", message.contains(hotelName));
         assertEquals("Unexpected number of bookings", --bookingCount, selenium.getCount(COUNT_BOOKING));
      }
   }

   protected void bookHotel(String hotelName, CreditCardType creditCardType)
   {
      if (!isLoggedIn())
      {
         fail();
      }
      if (!selenium.isElementPresent(SEARCH_QUERY))
      {
         selenium.open(contextPath);
         selenium.waitForPageToLoad();
         selenium.click(MENU_FIND);
         selenium.waitForPageToLoad();
      }
      enterSearchQuery(hotelName);
      selenium.click(SEARCH_RESULT_TABLE_FIRST_ROW_LINK);
      selenium.waitForPageToLoad();
      // booking page
      selenium.click(BUTTON_BOOK);
      selenium.waitForPageToLoad();
      // hotel page
      populateBookingFields(creditCardType);
      selenium.click(BUTTON_PROCEED);
      selenium.waitForPageToLoad();
      // confirm page
      selenium.click(BUTTON_CONFIRM);
      selenium.waitForPageToLoad();
      // main page
      assertTrue("Booking failed.", selenium.isTextPresent(BOOKING_MESSAGE));
   }

   protected void populateBookingFields(CreditCardType creditCardType)
   {
      selenium.select(DETAILS_CARD_TYPE, creditCardType.getLocator());
   }

   protected void populateBookingFields(String creditCardNumber, CreditCardType creditCardType)
   {
      selenium.type(DETAILS_CARD_NUMBER, creditCardNumber);
      populateBookingFields(creditCardType);
   }
   
   private enum CreditCardType
   {
      VISA ("VISA"),
      MASTERCARD ("MasterCard"),
      AMEX ("AMEX"),
      DISCOVER ("Discover");
      
      private String name ;

      private CreditCardType(String name)
      {
         this.name = name;
      }

      public String getName()
      {
         return name;
      }

      public OptionLocator<?> getLocator()
      {
         return new OptionValueLocator(name);
      }
   }
}
