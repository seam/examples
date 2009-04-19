
package org.jboss.seam.example.wicket;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.Link;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.example.wicket.action.HotelBooking;
import org.jboss.seam.wicket.annotations.NoConversationPage;

@Restrict("#{identity.loggedIn}")
@NoConversationPage(Main.class)
public class Hotel extends WebPage 
{

   @In
   private org.jboss.seam.example.wicket.action.Hotel hotel;
   
   @In(create=true)
   private HotelBooking hotelBooking;

	public Hotel(final PageParameters parameters)
	{
	   super(parameters);
	   Template body = new Template("body");
	   body.add(new Link("bookHotel")
	   {
	      @Override
	      public void onClick()
	      {
	         hotelBooking.bookHotel();
	         setResponsePage(new Book(new PageParameters()));
	      }
	   });
	   body.add(new Link("cancel")
      {
         @Override
         @End
         public void onClick()
         {
            setResponsePage(Main.class);
         }
         
      });
	   body.add(new HotelViewPanel("hotel", hotel));
	   add(body);
	}
	
	@Override
	protected void onBeforeRender()
	{
	   super.onBeforeRender();
	}
	
	
}
