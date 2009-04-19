//$Id$
package org.jboss.seam.example.spring;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.web.Session;

@Scope(ScopeType.SESSION)
@Synchronized
@Name("login")
public class LoginAction implements Serializable {

	@In(required = false)
	@Out(required = false)
	private User user;

	@In("#{userService}")
	private UserService userService;

	@In
	private FacesMessages facesMessages;

	private boolean loggedIn;

	public void login() {
		User candidateUser = userService.findUser(user.getUsername(), user.getPassword());

		if (candidateUser == null) {
			facesMessages.add("Invalid login");
		} else {
			user = candidateUser;
			loggedIn = true;
			facesMessages.add("Welcome, #{user.name}");
		}

	}

	public void logout() {
		loggedIn = false;
		Session.instance().invalidate();
	}

	public void validateLogin() {
		if (!loggedIn) {
			facesMessages.add("Please log in first");
		}
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

}
