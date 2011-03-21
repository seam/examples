package org.jboss.seam.example.gwt.helloworld.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import org.jboss.errai.cdi.client.api.Event;
import org.jboss.errai.ioc.client.api.EntryPoint;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * Welcome to Errai CDI!
 */
@EntryPoint
public class GwtHelloworld {
    @Inject
    private Event<MessageEvent> messageEvent;

    private final Label responseLabel = new Label();

    @PostConstruct
    public void buildUI() {
        final Button button = new Button("Send");
        final TextBox message = new TextBox();

        button.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                messageEvent.fire(new MessageEvent(message.getText()));
            }
        });

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(message);
        horizontalPanel.add(button);
        horizontalPanel.add(responseLabel);

        RootPanel.get().add(horizontalPanel);
    }

    public void response(@Observes ResponseEvent event) {
        responseLabel.setText(event.getMessage().toUpperCase());
    }
}