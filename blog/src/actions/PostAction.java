package actions;

import java.io.IOException;
import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.cache.CacheProvider;
import org.jboss.seam.faces.FacesMessages;

import domain.Blog;
import domain.BlogEntry;

/**
 * Handles submission of a new blog entry
 * 
 * @author Gavin King
 */
@Name("postAction")
@Scope(ScopeType.STATELESS)
public class PostAction
{
   @In Blog blog;
   
   @In EntityManager entityManager;
   
   @In(required=false) BlogEntry blogEntry;
   
   @In CacheProvider cacheProvider;
   
   public void post() throws IOException
   {
      blogEntry.setDate( new Date() );
      blog.getBlogEntries().add(blogEntry);
      entityManager.persist(blogEntry);
      cacheProvider.remove("pageFragments", "index");
   }
   
   public void invalid()
   {
      FacesMessages.instance().add("You are missing some information, please try again");
   }
   
}
