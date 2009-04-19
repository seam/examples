package org.jboss.seam.example.seamdiscs.action;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.seamdiscs.model.User;
import org.jboss.seam.framework.EntityController;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;


@Name("authenticator")
public class Authenticator extends EntityController
{
    @Logger Log log;
    
    @In Identity identity;
   
    public boolean authenticate()
    {
        log.info("authenticating #0", identity.getUsername());
        List<User> users = getEntityManager().createQuery("select u from User u where u.username = :username and u.password = :password").setParameter("username", identity.getUsername()).setParameter("password", identity.getPassword()).getResultList();
        if (users.size() == 1)
        {
           identity.addRole("admin");
           return true;
        } 
        else
        {
           return false;
        }
    }
}
