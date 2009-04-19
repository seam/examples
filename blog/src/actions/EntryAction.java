package actions;

import static org.jboss.seam.ScopeType.STATELESS;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

import domain.Blog;
import domain.BlogEntry;

/**
 * Processes a request for a particular entry,
 * and sends a 404 if none is found.
 * 
 * @author Gavin King
 */
@Name("entryAction")
@Scope(STATELESS)
public class EntryAction
{
   @In Blog blog;
   
   @Out BlogEntry blogEntry;
   
   public void loadBlogEntry(String id) throws EntryNotFoundException
   {
      blogEntry = blog.getBlogEntry(id);
      if (blogEntry==null) throw new EntryNotFoundException(id);
   }
   
}
