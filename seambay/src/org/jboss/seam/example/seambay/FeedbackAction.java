package org.jboss.seam.example.seambay;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;

@Name("feedbackAction")
public class FeedbackAction implements Serializable
{
   private static final long serialVersionUID = -7575590318129835094L;

   @DataModel
   private List<Feedback> memberFeedback;
   
   @In 
   EntityManager entityManager;
   
   @Factory("memberFeedback")
   public void getMemberFeedback()
   {
     memberFeedback = entityManager.createQuery(
           "from Feedback where forAccount = #{selectedMember}")
           .getResultList();       
   }
}
