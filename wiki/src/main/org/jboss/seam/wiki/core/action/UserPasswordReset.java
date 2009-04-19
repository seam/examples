/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.action.prefs.UserManagementPreferences;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.preferences.Preferences;
import org.jboss.seam.wiki.util.Hash;

import static org.jboss.seam.international.StatusMessage.Severity.ERROR;
import static org.jboss.seam.international.StatusMessage.Severity.WARN;
import static org.jboss.seam.international.StatusMessage.Severity.INFO;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The password reset feature works as follows:
 *
 * - User enters username and e-mail address. The account does not have to be activated, so
 *   the "Reset Password" functionality can also serve as "Resend Activation E-Mail".
 *
 * - Both username and e-mail address are checked with what we have in the database.
 *
 * - An activation code is generated and stored in the database for this user account.
 *
 * - An e-mail with the activation code is send to the users e-mail address.
 *
 * - If the user clicks on the activation link, the login form on the page will
 *   switch to a password reset form. (If the activation code was correct.)
 *
 * - After typing in the password twice, the user account gets a new password and
 *   we also activate it.
 *
 *
 * @author Christian Bauer
 */
@Name("userPasswordReset")
@Scope(ScopeType.CONVERSATION)
public class UserPasswordReset implements Serializable {

    public static final String RESET_PASSWORD_OF_USER = "resetPasswordOfUser";

    @Logger
    Log log;

    @In
    private StatusMessages statusMessages;

    @In("#{preferences.get('UserManagement')}")
    UserManagementPreferences prefs;

    @In(create = true)
    private Renderer renderer;

    @In
    private UserDAO userDAO;

    @In
    protected EntityManager entityManager;

    @In
    private Hash hashUtil;

    private String activationCode;
    private String username;
    private String email;

    private String password;
    private String passwordControl;

    public String getActivationCode() { return activationCode; }
    public void setActivationCode(String activationCode) { this.activationCode = activationCode; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPasswordControl() { return passwordControl; }
    public void setPasswordControl(String passwordControl) { this.passwordControl = passwordControl; }


    public void sendResetPasswordEmail() {
        log.debug("trying to reset password of user: " + username);

        User user = getUserForEmail(username, email);
        if (user == null) {
            statusMessages.addFromResourceBundleOrDefault(
                WARN,
                "lacewiki.msg.resetPassword.NotValid",
                "Your account and e-mail address information didn't match, please try again to reset your password."
            );
            username = null;
            email = null;
            return;
        }

        // Set activation code (unique user in time)
        String seed = user.getUsername() + System.currentTimeMillis() + prefs.getActivationCodeSalt();
        user.setActivationCode( ((Hash) Component.getInstance(Hash.class)).hash(seed) );
        // TODO: Flush by side effect?

        try {

            // Outject for email
            Contexts.getEventContext().set(RESET_PASSWORD_OF_USER, user);

            // Send confirmation email
            renderer.render("/themes/"
                    + Preferences.instance().get(WikiPreferences.class).getThemeName()
                    + "/mailtemplates/resetPassword.xhtml");

            statusMessages.addFromResourceBundleOrDefault(
                INFO,
                "lacewiki.msg.resetPassword.EmailSent",
                "A new activation code has been sent to your e-mail address, please read this e-mail to reset your password."
            );

        } catch (Exception ex) {
            statusMessages.add(WARN, "Couldn't send password reset email: " + ex.getMessage());
        }
    }

    public String prepare() {
        User user = userDAO.findUserWithActivationCode(activationCode);
        if (user != null) {
            log.debug("preparing password reset of: " + user);
            user.setActivationCode(null);
            // Outject for form
            Contexts.getSessionContext().set(RESET_PASSWORD_OF_USER, user);

            return "prepared";
        } else {
            return "notFound";
        }
    }

    public void reset() {
        User user = (User)Component.getInstance(RESET_PASSWORD_OF_USER);
        if (user == null) {
            throw new IllegalStateException("No user for password reset in SESSION context");
        }

        // Validate
        if (!passwordAndControlNotNull() ||
            !passwordMatchesRegex() ||
            !passwordMatchesControl()) {

            // Force re-entry
            setPassword(null);
            setPasswordControl(null);

            return;
        }
        log.debug("resetting password of: " + user);

        User persistentUser = userDAO.findUser(user.getId());
        persistentUser.setPasswordHash(hashUtil.hash(getPassword()));

        // As a side effect, also activate the user! http://jira.jboss.com/jira/browse/JBSEAM-2687
        persistentUser.setActivated(true);

        Contexts.getSessionContext().remove(RESET_PASSWORD_OF_USER);

        statusMessages.addFromResourceBundleOrDefault(
            INFO,
            "lacewiki.msg.resetPassword.Complete",
            "Successfully reset password of account '{0}', please log in.",
            persistentUser.getUsername()
        );

    }

    private User getUserForEmail(String username, String email) {
        if (User.GUEST_USERNAME.equals(username)) return null;
        User user = userDAO.findUser(username, false, true);
        return user != null && user.getEmail().equals(email) ? user : null;
    }

    public boolean passwordAndControlNotNull() {
        if (getPassword() == null || getPassword().length() == 0 ||
            getPasswordControl() == null || getPasswordControl().length() == 0) {
            statusMessages.addFromResourceBundleOrDefault(
                WARN,
                "lacewiki.msg.PasswordOrPasswordControlEmpty",
                "Please enter your password twice!"
            );
            return false;
        }
        return true;
    }

    public boolean passwordMatchesRegex() {
        Matcher matcher = Pattern.compile(prefs.getPasswordRegex()).matcher(getPassword());
        if (!matcher.find()) {
            statusMessages.addFromResourceBundleOrDefault(
                WARN,
                "lacewiki.msg.PasswordDoesntMatchPattern",
                "Password does not match the pattern: {0}",
                prefs.getPasswordRegex()
            );
            return false;
        }
        return true;
    }

    public boolean passwordMatchesControl() {
        if (!password.equals(passwordControl) ) {
            statusMessages.addFromResourceBundleOrDefault(
                WARN,
                "lacewiki.msg.PasswordControlNoMatch",
                "The passwords don't match."
            );
            return false;
        }
        return true;
    }

}
