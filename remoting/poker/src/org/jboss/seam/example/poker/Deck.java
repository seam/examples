package org.jboss.seam.example.poker;

import org.jboss.seam.annotations.Name;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * A deck of cards
 *
 * @author Shane Bryzak
 */
@Name("deck")
public class Deck
{
  /**
   * The cards remaining in the deck
   */
  private Queue<Card> cards = new LinkedList<Card>();

  /**
   * The complete contents of the deck
   */
  private Set<Card> contents;

  /**
   * Constructor, initialises the deck.
   *
   * @param contents Set The set of cards that the deck will contain
   */
  public Deck(Set<Card> contents)
  {
    this.contents = contents;
    reset();
  }

  /**
   * Resets the deck to its original contents
   */
  public void reset()
  {
    cards.clear();
    cards.addAll(contents);
  }

  /**
   * Shuffles the cards in the deck
   */
  public void shuffle()
  {
    Random rnd = new Random(System.currentTimeMillis());

    List<Card> tmp = new ArrayList<Card>();

    while (!cards.isEmpty())
      tmp.add(cards.poll());

    while (!tmp.isEmpty())
      cards.offer(tmp.remove(rnd.nextInt(tmp.size())));
  }

  /**
   * Returns the number of cards remaining in the deck.
   *
   * @return int
   */
  public int cardsRemaining()
  {
    return cards.size();
  }

  /**
   * Remove the next card from the deck
   *
   * @return Card
   */
  public Card remove()
  {
    return cards.poll();
  }
}
