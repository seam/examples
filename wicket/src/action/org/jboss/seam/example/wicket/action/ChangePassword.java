//$Id: ChangePassword.java,v 1.5 2007/06/27 00:06:49 gavin Exp $
package org.jboss.seam.example.wicket.action;

import javax.ejb.Local;

@Local
public interface ChangePassword
{
   public void changePassword();
   
   public void destroy();
}