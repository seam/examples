package org.jboss.seam.example.poker;

import org.jboss.seam.annotations.Name;

/**
 * A single playing card.
 *
 * @author Shane Bryzak
 */
@Name("card")
public class Card
{
  public enum Value {
    ace("A"),
    two("2"),
    three("3"),
    four("4"),
    five("5"),
    six("6"),
    seven("7"),
    eight("8"),
    nine("9"),
    ten("10"),
    jack("J"),
    queen("Q"),
    king("K"),
    joker("J");

    private String symbol;

    Value(String symbol)
    {
      this.symbol = symbol;
    }

    public String getSymbol()
    {
      return symbol;
    }
  }

  public enum Suit { heart, diamond, club, spade };

  private Value value;
  private Suit suit;

  public Card(Value value, Suit suit)
  {
    this.value = value;
    this.suit = suit;
  }

  public Value getValue()
  {
    return value;
  }

  public Suit getSuit()
  {
    return suit;
  }

  public String toString()
  {
    return value == Value.joker ? "Joker" : String.format("%s %s", value.getSymbol(), suit.toString());
  }
}
