package org.jboss.seam.security.examples.simple;

import javax.inject.Inject;

import org.jboss.solder.logging.Logger;
import org.jboss.seam.security.Authenticator;
import org.jboss.seam.security.BaseAuthenticator;
import org.jboss.seam.security.Credentials;
import org.picketlink.idm.impl.api.PasswordCredential;
import org.picketlink.idm.impl.api.model.SimpleUser;

/**
 * This is the simplest possible example of a custom authenticator.
 *
 * @author Shane Bryzak
 */
public class SimpleAuthenticator extends BaseAuthenticator implements Authenticator {
    private static final Logger log = Logger.getLogger(SimpleAuthenticator.class);

    @Inject
    Credentials credentials;

    @Override
    public void authenticate() {
        if ("demo".equals(credentials.getUsername()) &&
                credentials.getCredential() instanceof PasswordCredential &&
                "demo".equals(((PasswordCredential) credentials.getCredential()).getValue())) {
            setStatus(AuthenticationStatus.SUCCESS);
            setUser(new SimpleUser("demo"));            
            log.info("Authentication successful for user '" + credentials.getUsername() + "'");
        } else {                    
            setStatus(AuthenticationStatus.FAILURE);            
            log.info("Authentication failed for user '" + credentials.getUsername() + "'");            
        }
    }

}
