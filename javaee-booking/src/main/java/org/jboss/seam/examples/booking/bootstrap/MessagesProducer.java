package org.jboss.seam.examples.booking.bootstrap;

import java.io.Serializable;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.seam.international.status.Messages;

/**
 * Class created to supress the bug with the injection of messages.
 * 
 * @author jose.freitas
 * 
 */
@ConversationScoped
public class MessagesProducer implements Serializable {
	private static final long serialVersionUID = 1116960726951349427L;

	@Inject
	BeanManager beanManager;

	@Produces
	@MessagesAlternative
	public Messages createMessagesAlternative() {
		Bean messagesBean = beanManager.getBeans(Messages.class).iterator().next();
		CreationalContext cc = beanManager.createCreationalContext(messagesBean);
		return (Messages) beanManager.getReference(messagesBean, Messages.class, cc);
	}
}
