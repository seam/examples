package org.jboss.seam.examples.princessrescue.ftest;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.arquillian.ajocado.framework.AjaxSelenium;
import org.jboss.arquillian.ajocado.locator.IdLocator;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.jboss.arquillian.ajocado.locator.LocatorFactory.id;
import static org.jboss.arquillian.ajocado.Ajocado.waitForHttp;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Functional test for the PrincessRescue example
 *
 * @author Ondrej Skutka
 */
@RunWith(Arquillian.class)
public class PrincessRescueTest {
    private static final String MSG_INTRO = "The princess has been kidnaped by a dragon, and you are the only one who can save her. Armed only with your trusty bow you bravely head into the dragon's lair, who know what horrors you will encounter, some say the caves are even home to the dreaded Wumpus.";
    private static final String MSG_ENTRANCE = "You enter the dungeon, with you bow in your hand and your heart in your mouth.";

    private static final String MSG_NEAR_BATS = "You hear a screeching noise.";
    private static final String MSG_BATS = "A swarm of bats lands on you and tries to pick you up. They fail miserably. You swat them away with your bow.";

    private static final String MSG_NEAR_DWARF = "You hear drunken singing.";
    private static final String MSG_DWARF = "You find a drunken dwarven miner. He belches in your direction, falls over, then seems to forget you are there.";
    private static final String MSG_SHOT_DWARF = "You hear a 'Thud', followed by a surprised yell.";
    private static final String MSG_DEAD_DWARF = "You find a dead dwarven miner with something that looks suspiciously like one of your arrows sticking out of his chest. Probably best you don't mention this to anyone...";

    private static final String MSG_NEAR_PIT = "You feel a breeze.";
    private static final String MSG_PIT = "You fall into a bottomless pit. Game Over.";

    private static final String MSG_NEAR_PRINCESS = "You hear a sobbing noise.";
    private static final String MSG_PRINCESS = "You find the princess and quickly free her, and then escape from the dungeon. You both live happily ever after.";

    private static final String MSG_NEAR_DRAGON = "You hear a snoring noise. With every snore you see a flickering light, as if something were breathing flames from its nostrils.";
    private static final String MSG_SHOT_DRAGON = "Your arrow wakes up the dragon, without appearing to do any real damage. The last moments of your life are spent running from an angry dragon.";

    private static final String MAIN_PAGE = "/home.jsf";
    private IdLocator NEW_GAME_BUTTON = id("bv:next");
    
    public static final String ARCHIVE_NAME = "config-princess-rescue.war";
    public static final String BUILD_DIRECTORY = "target";

    protected enum Direction {
        NORTH, SOUTH, WEST, EAST
    }

    protected enum Action {
        MOVE, SHOT
    }
    
    @ArquillianResource
    URL contextPath;
    
    @Drone
    AjaxSelenium selenium;
    
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(ZipImporter.class, ARCHIVE_NAME).importFrom(new File(BUILD_DIRECTORY + '/' + ARCHIVE_NAME))
                .as(WebArchive.class);
    }

    @Before
    public void startNewGame() throws MalformedURLException {
        selenium.setSpeed(300);
        selenium.open(new URL(contextPath.toString() + MAIN_PAGE));
        ensureTextPresent(MSG_INTRO);
        waitForHttp(selenium).click(NEW_GAME_BUTTON);
        ensureTextPresent(MSG_ENTRANCE);
    }

    /**
     * Start the game, kill the dwarf and rescue the princess
     */
    @Test
    public void findPrincess() {
        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.NORTH));
        ensureTextPresent(MSG_NEAR_DWARF);
        ensureTextPresent(MSG_NEAR_PIT);

        // Take a look at the dwarf
        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.WEST));
        ensureTextPresent(MSG_DWARF);
        for (Action action : Action.values()) {
            assertTrue(selenium.isEditable(getLocator(action, Direction.EAST)));
            assertFalse(selenium.isEditable(getLocator(action, Direction.WEST)));
            assertFalse(selenium.isEditable(getLocator(action, Direction.NORTH)));
            assertFalse(selenium.isEditable(getLocator(action, Direction.SOUTH)));
        }

        // We can still hear the dwarf singing
        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        ensureTextPresent(MSG_NEAR_DWARF);
        ensureTextPresent(MSG_NEAR_PIT);

        // Kill the drunkard
        waitForHttp(selenium).click(getLocator(Action.SHOT, Direction.WEST));
        ensureTextPresent(MSG_SHOT_DWARF);

        // Bury the evidence
        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.WEST));
        ensureTextPresent(MSG_DEAD_DWARF);

        // No more bad singer
        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        assertFalse("Expected the dwarf to be dead already.",selenium.isTextPresent(MSG_NEAR_DWARF));
        ensureTextPresent(MSG_NEAR_PIT);

        // Now for the princess!
        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.NORTH));
        ensureTextPresent(MSG_NEAR_BATS);
        ensureTextPresent(MSG_NEAR_PIT);

        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        ensureTextPresent(MSG_BATS);
        ensureTextPresent(MSG_NEAR_PIT);

        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        ensureTextPresent(MSG_NEAR_BATS);

        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        ensureTextPresent(MSG_NEAR_DRAGON);

        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.NORTH));
        ensureTextPresent(MSG_NEAR_PRINCESS);

        // Happy end
        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        ensureTextPresent(MSG_PRINCESS);
        ensureButtonsDisabled();
    }

    /**
     * Start the game, tickle the dragon and die
     */
    @Test
    public void dieHeroically() {
        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.NORTH));
        ensureTextPresent(MSG_NEAR_DWARF);
        ensureTextPresent(MSG_NEAR_PIT);

        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.NORTH));
        ensureTextPresent(MSG_NEAR_BATS);
        ensureTextPresent(MSG_NEAR_PIT);

        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        ensureTextPresent(MSG_BATS);
        ensureTextPresent(MSG_NEAR_PIT);

        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        ensureTextPresent(MSG_NEAR_BATS);

        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        ensureTextPresent(MSG_NEAR_DRAGON);

        waitForHttp(selenium).click(getLocator(Action.SHOT, Direction.EAST));
        ensureTextPresent(MSG_SHOT_DRAGON);
        ensureButtonsDisabled();
    }

    /**
     * Start the game, enjoy a free fall.
     */
    @Test
    public void dieImpressively() {
        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.NORTH));
        ensureTextPresent(MSG_NEAR_DWARF);
        ensureTextPresent(MSG_NEAR_PIT);

        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.EAST));
        ensureTextPresent(MSG_PIT);
        ensureButtonsDisabled();
    }

    /**
     * Start the game, kill the dwarf, start over, ensure the dwarf is alive again (and drunken).
     */
    @Test
    public void quitEarly() {
        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.NORTH));
        ensureTextPresent(MSG_NEAR_DWARF);
        ensureTextPresent(MSG_NEAR_PIT);

        waitForHttp(selenium).click(getLocator(Action.SHOT, Direction.WEST));
        ensureTextPresent(MSG_SHOT_DWARF);

        waitForHttp(selenium).click(NEW_GAME_BUTTON);
        ensureTextPresent(MSG_INTRO);
        waitForHttp(selenium).click(NEW_GAME_BUTTON);
        ensureTextPresent(MSG_ENTRANCE);

        waitForHttp(selenium).click(getLocator(Action.MOVE, Direction.NORTH));
        ensureTextPresent(MSG_NEAR_DWARF);
        ensureTextPresent(MSG_NEAR_PIT);
    }

    /**
     * Ensures that all the move and shot buttons present on the page are disabled. Fails if not.
     */
    private void ensureButtonsDisabled() {
        for (Direction direction : Direction.values()) {
            for (Action action : Action.values()) {
                assertFalse(selenium.isEditable(getLocator(action, direction)));
            }
        }
    }

    /**
     * Ensures that the specified text is present on the page. Fails if not.
     */
    private void ensureTextPresent(String text) {
        assertTrue("Expected the following text to be present: \"" + text + "\"",selenium.isTextPresent(text));
    }

    /**
     * Returns the move or shot button specified by parameters.
     */
    private IdLocator getLocator(Action action, Direction direction) {
        return id("bv:" + action.toString().toLowerCase() + direction.toString().toLowerCase());
    }

}
