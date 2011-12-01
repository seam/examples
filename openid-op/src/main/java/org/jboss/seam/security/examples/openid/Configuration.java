package org.jboss.seam.security.examples.openid;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.jboss.seam.security.external.openid.api.OpenIdProviderConfigurationApi;

@Model
public class Configuration {
    @Inject
    private OpenIdProviderConfigurationApi confApi;

    public String getXrdsURL() {
        return confApi.getXrdsURL();
    }
}
