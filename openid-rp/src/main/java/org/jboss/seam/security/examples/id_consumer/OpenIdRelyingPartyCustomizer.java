package org.jboss.seam.security.examples.id_consumer;

import java.util.Properties;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.jboss.seam.security.external.openid.api.OpenIdRelyingPartyConfigurationApi;
import org.jboss.solder.servlet.event.Initialized;
import org.jboss.solder.resourceLoader.Resource;


public class OpenIdRelyingPartyCustomizer {
    @Inject
    @Resource("openIdRelayingParty.properties")
    private Properties properties;

    public void servletInitialized(@Observes @Initialized final ServletContext context, OpenIdRelyingPartyConfigurationApi op) {

        PropertyReader propertyReader = new PropertyReader(properties);

        op.setHostName(propertyReader.getString("hostName", "www.openid-rp.com"));
        op.setPort(propertyReader.getInt("port", 8080));
        op.setProtocol(propertyReader.getString("protocol", "http"));
    }

}
