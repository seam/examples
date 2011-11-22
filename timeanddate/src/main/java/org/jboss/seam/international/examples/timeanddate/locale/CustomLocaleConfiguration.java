package org.jboss.seam.international.examples.timeanddate.locale;

import javax.annotation.PostConstruct;

import org.jboss.seam.international.locale.LocaleConfiguration;

/**
 * @author <a href="http://community.jboss.org/people/maschmid">Marek Schmidt</a>
 */
public class CustomLocaleConfiguration extends LocaleConfiguration {

    @PostConstruct
    public void setup() {

        addSupportedLocaleKey("en");
        addSupportedLocaleKey("pt_BR");

    }
}