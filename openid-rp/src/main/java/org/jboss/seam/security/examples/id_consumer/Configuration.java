package org.jboss.seam.security.examples.id_consumer;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.jboss.seam.security.external.openid.api.OpenIdRelyingPartyConfigurationApi;

/**
 * This bean is used purely to provide information to the view layer
 */
@Model
public class Configuration {
    @Inject
    private OpenIdRelyingPartyConfigurationApi confApi;

    public String getRealm() {
        return confApi.getRealm();
    }

    public String getXrdsURL() {
        return confApi.getXrdsURL();
    }
}
