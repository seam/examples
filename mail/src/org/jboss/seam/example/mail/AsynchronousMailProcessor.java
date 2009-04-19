package org.jboss.seam.example.mail;

import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.async.Duration;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.log.*;

@Name("asynchronousMailProcessor")
@AutoCreate
public class AsynchronousMailProcessor
{
    private static final LogProvider log = Logging.getLogProvider(AsynchronousMailProcessor.class); 

    @Asynchronous
    public void scheduleSend(@Duration long delay, Person person) {
	try {
	    Contexts.getEventContext().set("person", person);
	    Renderer.instance().render("/simple.xhtml");
	} catch (Exception e) {
	    log.error("Error scheduling send #0",e);
	}
    }
}
