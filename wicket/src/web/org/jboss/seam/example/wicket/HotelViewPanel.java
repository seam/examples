package org.jboss.seam.example.wicket;

import java.text.DecimalFormat;
import java.util.Locale;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.jboss.seam.example.wicket.action.Hotel;

public class HotelViewPanel extends Panel
{

   public HotelViewPanel(String id, Hotel hotel)
   {
      super(id);
      add(new OutputBorder("hotelNameBorder", "Name", new Label("hotelName", hotel.getName())));
      add(new OutputBorder("hotelAddressBorder", "Address", new Label("hotelAddress", hotel.getAddress())));
      add(new OutputBorder("hotelCityBorder", "City", new Label("hotelCity", hotel.getCity())));
      add(new OutputBorder("hotelStateBorder", "State", new Label("hotelState", hotel.getState())));
      add(new OutputBorder("hotelZipBorder", "Zip", new Label("hotelZip", hotel.getZip())));
      add(new OutputBorder("hotelCountryBorder", "Country", new Label("hotelCountry", hotel.getCountry())));
      add(new OutputBorder("hotelPriceBorder", "Nightly Rate", new Label("hotelPrice", DecimalFormat.getCurrencyInstance(Locale.US).format(hotel.getPrice()))));
   }

}
