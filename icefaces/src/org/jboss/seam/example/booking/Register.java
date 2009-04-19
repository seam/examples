//$Id: Register.java,v 1.5 2007/06/27 00:06:49 gavin Exp $
package org.jboss.seam.example.booking;

import javax.ejb.Local;

@Local
public interface Register
{
   public void register();
   public void invalid();
   public String getVerify();
   public void setVerify(String verify);
   public boolean isRegistered();
   
   public void destroy();
}