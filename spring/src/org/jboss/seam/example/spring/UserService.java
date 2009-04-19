package org.jboss.seam.example.spring;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mike Youngstrom
 *
 */
public class UserService {

	@PersistenceContext
    private EntityManager entityManager;

	@Transactional
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        System.out.println("change password " + oldPassword + " to " + newPassword);
        if (newPassword == null || newPassword.length()==0) {
            throw new IllegalArgumentException("newPassword cannot be null.");
        }

        User user = findUser(username);
        System.out.println("USER" + user);
        if (user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            return true;
        } else {
            return false;
        }
    }

	@Transactional
    public User findUser(String username) {
        if (username == null || "".equals(username)) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        return entityManager.find(User.class, username);
    }

	@Transactional
    public User findUser(String username, String password) {
        try {
            return (User) 
            entityManager.createQuery("select u from User u where u.username=:username and u.password=:password")
            .setParameter("username", username)
            .setParameter("password", password)
            .getSingleResult();
        } catch (PersistenceException e) {
            return null;
        }
    }

	@Transactional
    public void createUser(User user) throws ValidationException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        User existingUser = findUser(user.getUsername());
        if (existingUser != null) {
            throw new ValidationException("Username "+user.getUsername()+" already exists");
        }
        entityManager.persist(user);
    }
}
