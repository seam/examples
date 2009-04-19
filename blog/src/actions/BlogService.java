package actions;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

import domain.Blog;

/**
 * Singleton for the blog instance.
 *
 * @author Gavin King
 */
@Name("blog")
@Scope(ScopeType.STATELESS)
@AutoCreate
public class BlogService 
{
   
   @In EntityManager entityManager;
  
   @Unwrap
   public Blog getBlog()
   {
      return (Blog) entityManager.createQuery("select distinct b from Blog b left join fetch b.blogEntries")
            .setHint("org.hibernate.cacheable", true)
            .getSingleResult();
   }

}
