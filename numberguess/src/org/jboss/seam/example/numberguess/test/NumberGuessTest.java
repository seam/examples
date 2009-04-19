//$Id$
package org.jboss.seam.example.numberguess.test;

import org.jboss.seam.core.Manager;
import org.jboss.seam.pageflow.Pageflow;
import org.jboss.seam.example.numberguess.NumberGuess;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class NumberGuessTest extends SeamTest
{
   
   private int guess;
   
   @Test
   public void testNumberGuessWin() throws Exception
   {
      String id = new NonFacesRequest("/numberGuess.jspx")
      {

         @Override
         protected void renderResponse() throws Exception {
            NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
            assert ng.getMaxGuesses()==10;
            assert ng.getBiggest()==100;
            assert ng.getSmallest()==1;
            assert ng.getCurrentGuess()==null;
            assert ng.getGuessCount()==0;
            assert Manager.instance().isLongRunningConversation();
            assert Pageflow.instance().getProcessInstance().getRootToken()
                  .getNode().getName().equals("displayGuess");
         }
         
      }.run();

      String id2 = new FacesRequest("/numberGuess.jspx", id)
      {

         @Override
         protected void applyRequestValues() throws Exception {
            NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
            guess = ng.getRandomNumber() > 50 ? 25 : 75;
            ng.setCurrentGuess(guess);
         }

         @Override
         protected void invokeApplication() throws Exception {
            setOutcome("guess");
            //ng.guess();
         }
         
         @Override
         protected void afterRequest() {
            assert !isRenderResponseBegun();
            assert getViewId().equals("/numberGuess.jspx");
         }
         
      }.run();
      
      assert id2.equals(id);
      
      new NonFacesRequest("/numberGuess.jspx", id)
      {
         
         @Override
         protected void renderResponse() throws Exception {
            NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
            assert ng.getMaxGuesses()==10;
            assert ( guess > ng.getRandomNumber() && ng.getBiggest()==guess-1 ) 
                  || ( guess < ng.getRandomNumber() && ng.getSmallest()==guess+1 );
            assert !ng.isCorrectGuess();
            assert !ng.isLastGuess();
            assert ng.getCurrentGuess()==guess;
            assert ng.getGuessCount()==1;
            assert ng.getRemainingGuesses()==9;
            assert Manager.instance().isLongRunningConversation();
            assert Pageflow.instance().getProcessInstance().getRootToken()
                  .getNode().getName().equals("displayGuess");
         }
         
      }.run();

      id2 = new FacesRequest("/numberGuess.jspx", id)
      {

         @Override
         protected void applyRequestValues() throws Exception {
            NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
            ng.setCurrentGuess( ng.getRandomNumber() );
         }

         @Override
         protected void invokeApplication() throws Exception {
            setOutcome("guess");
            //ng.guess();
         }
         
         @Override
         protected void afterRequest()
         {
            assert !isRenderResponseBegun();
            assert getViewId().equals("/win.jspx");
         }
         
      }.run();
      
      assert id2.equals(id);
      
      new NonFacesRequest("/win.jspx", id)
      {
         @Override
         protected void renderResponse() throws Exception {
            NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
            assert ng.getMaxGuesses()==10;
            assert ng.isCorrectGuess();
            assert ng.getCurrentGuess()==ng.getRandomNumber();
            assert ng.getGuessCount()==2;
            assert !Manager.instance().isLongRunningConversation();
            assert Pageflow.instance().getProcessInstance().getRootToken()
                  .getNode().getName().equals("win");
         }
         
      }.run();

   }
   
   @Test
   public void testNumberGuessLose() throws Exception
   {
      String id = new NonFacesRequest("/numberGuess.jspx")
      {

         @Override
         protected void renderResponse() throws Exception {
            NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
            assert ng.getMaxGuesses()==10;
            assert ng.getBiggest()==100;
            assert ng.getSmallest()==1;
            assert ng.getCurrentGuess()==null;
            assert ng.getGuessCount()==0;
            assert Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      for (int i=1; i<=9; i++)
      {
         
         final int count = i;

         new FacesRequest("/numberGuess.jspx", id)
         {
   
            @Override
            protected void applyRequestValues() throws Exception {
               NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
               guess = ng.getRandomNumber() > 50 ? 25+count : 75-count;
               ng.setCurrentGuess(guess);
            }
   
            @Override
            protected void invokeApplication() throws Exception {
               setOutcome("guess");
               //ng.guess();
               //assert Pageflow.instance().getProcessInstance().getRootToken()
//                     .getNode().getName().equals("displayGuess");
            }
            
            @Override
            protected void afterRequest()
            {
               assert !isRenderResponseBegun();
               assert getViewId().equals("/numberGuess.jspx");
            }
            
         }.run();
         
         new NonFacesRequest("/numberGuess.jspx", id)
         {
   
            @Override
            protected void renderResponse() throws Exception {
               NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
               assert ng.getMaxGuesses()==10;
               assert ( guess > ng.getRandomNumber() && ng.getBiggest()==guess-1 ) 
                     || ( guess < ng.getRandomNumber() && ng.getSmallest()==guess+1 );
               assert !ng.isCorrectGuess();
               assert !ng.isLastGuess();
               assert ng.getCurrentGuess()==guess;
               assert ng.getGuessCount()==count;
               assert ng.getRemainingGuesses()==10-count;
               assert Manager.instance().isLongRunningConversation();
               assert Pageflow.instance().getProcessInstance().getRootToken()
                     .getNode().getName().equals("displayGuess");
            }
            
         }.run();
      
      }

      new FacesRequest("/numberGuess.jspx", id)
      {

         @Override
         protected void applyRequestValues() throws Exception {
            NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
            guess = ng.getRandomNumber() > 50 ? 49 : 51;
            ng.setCurrentGuess(guess);
         }

         @Override
         protected void invokeApplication() throws Exception {
            setOutcome("guess");
            //ng.guess();
            assert Pageflow.instance().getProcessInstance().getRootToken()
                  .getNode().getName().equals("displayGuess");
         }

         @Override
         protected void afterRequest()
         {
            assert !isRenderResponseBegun();
            assert getViewId().equals("/lose.jspx");
         }
         
      }.run();

      new NonFacesRequest("/lose.jspx", id)
      {

         @Override
         protected void renderResponse() throws Exception {
            NumberGuess ng = (NumberGuess) getInstance(NumberGuess.class);
            assert ng.getMaxGuesses()==10;
            assert ( guess > ng.getRandomNumber() && ng.getBiggest()==guess-1 ) 
                  || ( guess < ng.getRandomNumber() && ng.getSmallest()==guess+1 );
            assert !ng.isCorrectGuess();
            assert ng.isLastGuess();
            assert ng.getCurrentGuess()==guess;
            assert ng.getGuessCount()==10;
            assert ng.getRemainingGuesses()==0;
            assert !Manager.instance().isLongRunningConversation();
            assert Pageflow.instance().getProcessInstance().getRootToken()
                  .getNode().getName().equals("lose");
         }
         
      }.run();

   }
   
}
