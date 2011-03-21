package org.jboss.seam.example.gwt.helloworld.server;

import org.jboss.seam.example.gwt.helloworld.client.MessageEvent;
import org.jboss.seam.example.gwt.helloworld.client.ResponseEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

/**
 * Generated
 */
@ApplicationScoped
public class GwtHelloworldService {
    @Inject
    private  Event<ResponseEvent> responseEvent;

    public void handleMessage(@Observes MessageEvent event) {
        System.out.println("Received Message: " + event.getMessage());
        responseEvent.fire(new ResponseEvent(event.getMessage() + ":" + System.currentTimeMillis()));
    }
}