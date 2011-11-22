package org.jboss.seam.international.examples.timeanddate.locale;

import java.util.List;
import java.util.Locale;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.jboss.seam.international.Alter;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.solder.core.Client;

/**
 * LocaleBean demonstrating injection of the current and available locales and changing the client locale.
 * 
 * @author <a href="http://community.jboss.org/people/maschmid">Marek Schmidt</a>
 */
@Model
public class LocaleBean {
    
    @Inject
    @Alter
    @Client
    private Event<Locale> localeEvent;
    
    @Inject
    @Client
    private Instance<Locale> userLocale;
    
    @Inject
    private List<Locale> availableLocales;
    
    @Inject
    Messages messages;
    
    public Locale getUserLocale() {        
        return userLocale.get();
    }
    
    public void setUserLocale(Locale locale) {        
        localeEvent.fire(locale);
        messages.info(new BundleKey("resources", "message.localechange"), locale.getDisplayName(locale));
    }
    
    public List<Locale> getAvailableLocales() {
        return availableLocales;
    }
}
