package org.jboss.seam.example.seambay;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("bidHistory")
public class BidHistory
{
   @In
   private EntityManager entityManager;
   
   @SuppressWarnings("unchecked")
   @Factory("history")
   public List<Bid> getHistory()
   {
      return entityManager.createQuery("from Bid where auction = #{auction}")
          .getResultList();      
   }
}
