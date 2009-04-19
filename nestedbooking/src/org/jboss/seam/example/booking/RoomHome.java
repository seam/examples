package org.jboss.seam.example.booking;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.framework.EntityHome;

import org.jboss.seam.example.booking.Room;

@Name("roomHome")
public class RoomHome extends EntityHome<Room>
{

   @RequestParameter
   private Long roomId;

   @Override
   public Object getId() 
   { 
      if (roomId==null)
      {
         return super.getId();
      }
      else
      {
         return roomId;
      }
   }

   @Override @Begin
   public void create() 
   {
      super.create();
   }

}
