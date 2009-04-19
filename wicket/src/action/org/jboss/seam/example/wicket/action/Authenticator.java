package org.jboss.seam.example.wicket.action;

import javax.ejb.Local;

@Local
public interface Authenticator
{
   boolean authenticate();
}
