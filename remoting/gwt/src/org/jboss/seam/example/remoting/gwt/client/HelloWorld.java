package org.jboss.seam.example.remoting.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * This is the "main" entry point, as per GWT.
 * Generally this is a lean class, you tend to use seperate widget classes after this point.
 * This is pretty much boiler plate, you can mostly ignore this.
 * 
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class HelloWorld implements EntryPoint {

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    // Assume that the host HTML has elements defined whose
    // IDs are "slot1", "slot2".  In a real app, you probably would not want
    // to hard-code IDs.  Instead, you could, for example, search for all 
    // elements with a particular CSS class and replace them with widgets.
    //
    RootPanel.get("slot1").add(new AskQuestionWidget());

  }
}
