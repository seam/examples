package com.jboss.dvd.seam;

import javax.ejb.Local;

@Local
public interface Authenticator
{
  boolean authenticate();
}
