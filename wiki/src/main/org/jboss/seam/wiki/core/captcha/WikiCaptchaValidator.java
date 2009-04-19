/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.captcha;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Validator;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Logging;
import org.jboss.seam.log.Log;
import org.jboss.seam.core.Interpolator;

import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * I don't trust the built-in validator with @CaptchaResponse.
 *
 * First, it seems to call captcha.validateResponse() twice on a form submit.
 * Second, sometimes my message vanishes and I get the default message ("incorrect response").
 * I can't reproduce that, so I've given up.
 *
 * @author Christian Bauer
 */
@Name("wikiCaptchaValidator")
@Validator(id = "wikiCaptchaValidator")
@BypassInterceptors
public class WikiCaptchaValidator implements javax.faces.validator.Validator, Serializable {

    public static final String VERIFICATION_MSG_EXPR = "#{messages['lacewiki.label.VerificationError']}";

    Log log = Logging.getLog(WikiCaptchaValidator.class);

    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {
        WikiCaptcha captcha = (WikiCaptcha)WikiCaptcha.instance();

        String response = (String)o;
        String challenge = captcha.getProtectedChallenge();

        log.debug("verifying captcha response: " + response + " against challenge: " + challenge);
        boolean valid = response != null && challenge != null && response.trim().equals(challenge);
        if (!valid) {
            log.debug("response is not valid, initializing with new challenge");
            captcha.init();
            FacesMessage msg = new FacesMessage();
            msg.setSummary(Interpolator.instance().interpolate(VERIFICATION_MSG_EXPR));
            msg.setSeverity(FacesMessage.SEVERITY_WARN);
            throw new ValidatorException(msg);
        }
        log.debug("response is valid");
    }
}

