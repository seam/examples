//$Id$
package org.jboss.seam.example.registration;

import javax.ejb.Local;

@Local
public interface Register
{
   public String register();
}