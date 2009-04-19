package org.jboss.seam.example.remoting.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * This shows how to do a a "composite" widget in GWT, as well as how to call back to the server.
 * @author Michael Neale
 */
public class AskQuestionWidget extends Composite
{
   private AbsolutePanel panel = new AbsolutePanel();
   
   public AskQuestionWidget() 
   {      
      Label lbl = new Label("OK, what do you want to know?");
      panel.add(lbl);
      final TextBox box = new TextBox();
      box.setText("What is the meaning of life?");
      panel.add(box);
      Button ok = new Button("Ask");
      ok.addClickListener(new ClickListener() 
      {
         public void onClick(Widget w)
         {
            ValidationUtility valid = new ValidationUtility();
            if (!valid.isValid(box.getText())) 
            {
               Window.alert("A question has to end with a '?'");
            } 
            else 
            {
               askServer(box.getText());
            } 
         }
      });
      panel.add(ok);
      
      initWidget(panel);
   }

   /** Now lets actually go to the server, using a callback - its called Ajax for a reason ! */
   private void askServer(String text)
   {
      getService().askIt(text, new AsyncCallback() 
      {
         public void onFailure(Throwable t)
         {
            Window.alert(t.getMessage());
         }

         public void onSuccess(Object data)
         {
            Window.alert((String) data);
         }         
      });      
   }
   
   /**
    * This gets the async service client stub. 
    */
   private MyServiceAsync getService() 
   {       
      String endpointURL = GWT.getModuleBaseURL() + "seam/resource/gwt";      
      
      MyServiceAsync svc = (MyServiceAsync) GWT.create(MyService.class);
      ((ServiceDefTarget) svc).setServiceEntryPoint(endpointURL);
      return svc;     
   }   
}
