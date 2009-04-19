package org.jboss.seam.example.poker;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.seam.ScopeType.APPLICATION;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Create;

/**
 * The game.  This is where everything happens.
 *
 * @author Shane Bryzak
 */
@Name("game")
@Scope(APPLICATION)
@Startup
public class Game
{
  private List<String> players = new ArrayList<String>();

  @Create
  public void createGame()
  {
    players.clear();
  }

  public synchronized boolean login(String playerName)
  {
    if (!players.contains(playerName))
    {
      players.add(playerName);
      return true;
    }
    else
      return false;
  }


}
