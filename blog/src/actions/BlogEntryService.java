package actions;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import domain.Blog;
import domain.BlogEntry;

@Name("blogEntryService")
@Scope(ScopeType.STATELESS)
public class BlogEntryService
{
   
   @In Blog blog;
   
   @Factory("blogEntry")
   public BlogEntry createBlogEntry()
   {
      return new BlogEntry(blog);
   }

}
