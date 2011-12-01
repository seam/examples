package org.jboss.seam.security.examples.openid;

import java.util.Properties;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.jboss.seam.security.external.openid.api.OpenIdProviderConfigurationApi;
import org.jboss.seam.servlet.event.Initialized;
import org.jboss.seam.solder.resourceLoader.Resource;


public class OpenIdProviderCustomizer {
    @Inject
    @Resource("openIdProviderCustomizer.properties")
    private Properties properties;

    public void servletInitialized(@Observes @Initialized final ServletContext context, OpenIdProviderConfigurationApi op) {

        PropertyReader propertyReader = new PropertyReader(properties);

        op.setHostName(propertyReader.getString("hostName", "www.openid-op.com"));
        op.setPort(propertyReader.getInt("port", 8080));
        op.setProtocol(propertyReader.getString("protocol", "http"));
    }

}
