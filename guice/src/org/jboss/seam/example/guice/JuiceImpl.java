package org.jboss.seam.example.guice;

/**
 * @author Pawel Wrzeszcz (pwrzeszcz [at] jboss . org)
 */
public class JuiceImpl implements Juice
{
   private String name;
   private int price;

   public JuiceImpl(String name, int price)
   {
      this.name = name;
      this.price = price;
   }

    public String getName()
   {
      return name;
   }

   public int getPrice()
   {
      return price;
   }

   @Override
   public String toString()
   {
      return name + " (" + price + " cents)";
   }
}
