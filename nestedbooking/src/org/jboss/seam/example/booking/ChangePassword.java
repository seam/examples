//$Id$
package org.jboss.seam.example.booking;

import javax.ejb.Local;

@Local
public interface ChangePassword
{
   public void changePassword();
   public boolean isChanged();
   
   public String getVerify();
   public void setVerify(String verify);
   
   public void destroy();
}