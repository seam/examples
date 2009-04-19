package org.jboss.seam.example.wicket;



import org.jboss.seam.wicket.SeamWebApplication;

/**
 * Port of Booking Application to Wicket
 */
public class WicketBookingApplication extends SeamWebApplication 
{

	@Override
	public Class getHomePage() 
	{
		return Home.class;
	}

   @Override
   protected Class getLoginPage()
   {
      return Home.class;
   }
	
}
