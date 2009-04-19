package domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class HitCount
{
   @Id
   @Column(name="blog_name")
   private String blogName;
   
   @OneToOne(optional=false)
   @JoinColumn(insertable=false, updatable=false)
   private Blog blog;
   
   private int pageviews;
   
   public int getPageviews()
   {
      return pageviews;
   }
   public void hit()
   {
      pageviews++;
   }
}
