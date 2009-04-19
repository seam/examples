//$Id: Register.java,v 1.1 2007/06/23 18:33:59 pmuir Exp $
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