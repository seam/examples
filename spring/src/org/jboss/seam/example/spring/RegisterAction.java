//$Id$
package org.jboss.seam.example.spring;

import static org.jboss.seam.ScopeType.EVENT;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

@Scope(EVENT)
@Name("register")
public class RegisterAction
{
    @In
    private User user;

    @In("#{userService}")
    private UserService userService;

    @In("#{hibernateTestService}")
    private HibernateTestService hibernateTestService;

    @SuppressWarnings("unused")
    @In("hotelSearch")
    private HotelSearchingAction hotelSearchingAction;

    @In
    private FacesMessages facesMessages;

    private String verify;

    private boolean registered;

    public void register()
    {
        if ( user.getPassword().equals(verify) )
        {

            try {
                userService.createUser(user);
                hibernateTestService.testHibernateIntegration();
                registered = true;
            } catch(ValidationException e) {
                facesMessages.add(e.getMessage());
            }
        }
        else
        {
            facesMessages.add("verify", "Re-enter your password");
            verify=null;
        }
    }

    public void invalid()
    {
        facesMessages.add("Please try again");
    }

    public boolean isRegistered()
    {
        return registered;
    }

    public String getVerify()
    {
        return verify;
    }

    public void setVerify(String verify)
    {
        this.verify = verify;
    }

}
