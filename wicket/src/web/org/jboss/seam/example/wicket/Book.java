package org.jboss.seam.example.wicket;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.extensions.yui.calendar.DateField;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.example.wicket.action.Booking;
import org.jboss.seam.example.wicket.action.HotelBooking;
import org.jboss.seam.wicket.annotations.NoConversationPage;

@Restrict("#{identity.loggedIn}")
@NoConversationPage(Main.class)
public class Book extends WebPage 
{
   
   private static final List<String> bedOptionsDisplayValues = Arrays.asList("One king-sized bed", "Two double beds", "Three beds");
   private static final List<Integer> bedOptions = Arrays.asList(1, 2, 3);
   private static final List<String> monthOptions = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
   private static final List<Integer> yearOptions = Arrays.asList(2008, 2009);
   
   @In
   private Booking booking;
   
   @In(create=true)
   private HotelBooking hotelBooking;

	public Book(final PageParameters parameters)
	{
	   super(parameters);
	   Template body = new Template("body");
	   add(body);
	   body.add(new HotelViewPanel("hotelView", booking.getHotel()));
	   body.add(new HotelBookingForm("booking"));
	}
	
	public class HotelBookingForm extends Form
	{
	   
      public HotelBookingForm(String id)
      {
         super(id);
         add(new ComponentFeedbackPanel("messages", this));
         add(new FormInputBorder("checkinDateBorder", "Check in date", new DateField("checkinDate").setRequired(true), new PropertyModel(booking, "checkinDate"), false));
         add(new FormInputBorder("checkoutDateBorder", "Check out date", new DateField("checkoutDate").setRequired(true), new PropertyModel(booking, "checkoutDate"), false));
         add(new FormInputBorder("bedsBorder", "Room Preference", new DropDownChoice("beds", bedOptions, new IChoiceRenderer()
         {

            public Object getDisplayValue(Object object)
            {
               return bedOptionsDisplayValues.get(((Integer) object - 1));
            }

            public String getIdValue(Object object, int index)
            {
               return object.toString();
            }
            
         }
         ).setRequired(true), new PropertyModel(booking, "beds")));
         add(new FormInputBorder("smokingBorder", "Smoking Preference", new RadioChoice("smoking", Arrays.asList(new Boolean[] {true, false}), new IChoiceRenderer()
         {

            public Object getDisplayValue(Object object)
            {
               if (new Boolean(true).equals(object))
               {
                  return "Smoking";
               }
               else
               {
                  return "Non Smoking";
               }
            }

            public String getIdValue(Object object, int index)
            {
               if (new Boolean(true).equals(object))
               {
                  return "true";
               }
               else
               {
                  return "false";
               }
            }
            
         }), new PropertyModel(booking, "smoking"), false));
         add(new FormInputBorder("creditCardBorder", "Credit Card #", new TextField("creditCard").setRequired(true), new PropertyModel(booking, "creditCard")));
         add(new FormInputBorder("creditCardNameBorder", "Credit Card Name", new TextField("creditCardName").setRequired(true), new PropertyModel(booking, "creditCardName")));
         add(new FormInputBorder("creditCardExpiryBorder", "Credit Card Expiry", new DropDownChoice("creditCardExpiryMonth", monthOptions).setRequired(true), new PropertyModel(booking, "creditCardExpiryMonth")).add(new DropDownChoice("creditCardExpiryYear", yearOptions).setRequired(true), new PropertyModel(booking, "creditCardExpiryYear")));
         add(new Link("cancel")
         {

            @Override
            @End
            public void onClick()
            {
               setResponsePage(Main.class);
            }
            
         });          
      }
      
      
      
      @Override
      protected void onSubmit()
      {
         hotelBooking.setBookingDetails();
         if (hotelBooking.isBookingValid())
         {
            setResponsePage(new Confirm(new PageParameters()));
         }
      }

	   
	}
	
}
