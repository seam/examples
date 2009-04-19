package actions;

import static org.jboss.seam.ScopeType.EVENT;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Identity;

import domain.Blog;

@Scope(EVENT)
@Name("authenticator")
public class Authenticator
{
   @In Blog blog;
   @In Identity identity;
   
   public boolean authenticate()
   {
      return blog.getPassword().equals(identity.getPassword());
   }
}
