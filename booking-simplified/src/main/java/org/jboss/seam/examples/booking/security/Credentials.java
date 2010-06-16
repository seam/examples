package org.jboss.seam.examples.booking.security;

public interface Credentials {
   String getUsername();
   void setUsername(String username);
   String getPassword();
   void setPassword(String password);
}
