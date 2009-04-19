package org.jboss.seam.example.openid;

import org.jboss.seam.annotations.*;
import org.jboss.seam.security.openid.*;

@Name("authenticator")
public class Auth
{
    @In(create=false) OpenId openid;

    public boolean authenticate()
    {
        System.out.println("AUTH: " + openid + "-" + openid.getValidatedId());
        
        return true;
    }


    public boolean authenticateOpenID() {
        return true;
    }

}
