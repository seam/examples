package org.jboss.seam.examples.booking.action;

import java.util.List;
import javax.ejb.Local;
import org.jboss.seam.examples.booking.model.Hotel;

public
@Local
interface HotelSearch
{
   public void find();

   public void previousPage();

   public void nextPage();

   public List<Hotel> getHotels();

   public boolean isNextPageAvailable();

   public boolean isPreviousPageAvailable();

   public void destroy();
}
