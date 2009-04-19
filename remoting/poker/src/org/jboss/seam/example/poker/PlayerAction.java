package org.jboss.seam.example.poker;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import static org.jboss.seam.ScopeType.APPLICATION;
import javax.ejb.Stateless;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Context;

/**
 * The player action bean
 *
 * @author Shane Bryzak
 */
@Name("playerAction")
@Stateless
public class PlayerAction implements PlayerLocal
{
  @In(scope = APPLICATION) Game game;

  public boolean login(String playerName)
  {
    Context ctx = Contexts.getApplicationContext();
    return game.login(playerName);
  }

//  @Remove @Destroy
//  public void remove() {}
}
