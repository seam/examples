package org.jboss.seam.example.booking;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;

@Name("roomList")
public class RoomList extends EntityQuery
{
   @Override
   public String getEjbql() 
   { 
      return "select room from Room room";
   }
}
