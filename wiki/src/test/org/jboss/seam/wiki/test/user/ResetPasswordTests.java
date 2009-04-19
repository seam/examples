/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.user;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.action.UserPasswordReset;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.jboss.seam.wiki.util.Hash;
import org.testng.annotations.Test;

/**
 * @author Christian Bauer
 */
public class ResetPasswordTests  extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void resetPassword() throws Exception {

        new NonFacesRequest("/resetPassword.xhtml") {
            protected void beforeRequest() {
                setParameter("activationCode", "ebb0bce9eeeee191e8089afd1120c4a7");
            }

            protected void renderResponse() throws Exception {
                User user = (User)getInstance(UserPasswordReset.RESET_PASSWORD_OF_USER);
                assert user.getId().equals(3l);

            }
        }.run();

        new FacesRequest("/wiki.xhtml") {

            protected void invokeApplication() throws Exception {
                UserPasswordReset reset = (UserPasswordReset)getInstance(UserPasswordReset.class);
                reset.setPassword("foo123");
                reset.setPasswordControl("foo123");

                reset.reset();
            }

            protected void renderResponse() throws Exception {
                User user = (User)getInstance(UserPasswordReset.RESET_PASSWORD_OF_USER);
                assert user == null;

                UserDAO dao = (UserDAO)getInstance(UserDAO.class);
                User dbUser = dao.findUser(3l);
                assert dbUser.getActivationCode() == null;
                Hash hashUtil = (Hash)getInstance(Hash.class);
                assert dbUser.getPasswordHash().equals(hashUtil.hash("foo123"));
            }
        }.run();

    }
}
