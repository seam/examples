//$Id$
package org.jboss.seam.example.messages;

import javax.ejb.Local;

@Local
public interface MessageManager
{
   public void findMessages();
   public void select();
   public void delete();
   public void destroy();
}