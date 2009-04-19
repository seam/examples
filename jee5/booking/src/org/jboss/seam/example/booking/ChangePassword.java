//$Id: ChangePassword.java,v 1.1 2007/06/23 18:33:59 pmuir Exp $
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