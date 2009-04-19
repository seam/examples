package org.jboss.seam.example.poker;

import javax.ejb.Local;

import org.jboss.seam.annotations.remoting.WebRemote;

/**
 * Local interface for player actions
 *
 * @author Shane Bryzak
 */
@Local
public interface PlayerLocal
{
  @WebRemote
  boolean login(String playerName);
}
