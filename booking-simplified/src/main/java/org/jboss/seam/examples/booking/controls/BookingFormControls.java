package org.jboss.seam.examples.booking.controls;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.faces.component.UIComponent;

/**
 * A UI binding bean that can provide access to the local id and client id
 * of selected input components in the booking form.
 *
 * @author Dan Allen
 */
@Named
@RequestScoped
public class BookingFormControls implements Serializable
{
   private UIComponent checkinDate;

   private UIComponent checkoutDate;

   public UIComponent getCheckinDate()
   {
      return checkinDate;
   }

   public String getCheckinDateControlId()
   {
      return checkinDate.getClientId();
   }

   public void setCheckinDate(UIComponent checkinDate)
   {
      this.checkinDate = checkinDate;
   }

   public UIComponent getCheckoutDate()
   {
      return checkoutDate;
   }

   public void setCheckoutDate(UIComponent checkoutDate)
   {
      this.checkoutDate = checkoutDate;
   }

   public String getCheckoutDateControlId()
   {
      return checkoutDate.getClientId();
   }
}
