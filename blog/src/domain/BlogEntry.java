package domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;

/**
 * Represents a blog entry.
 *
 * @author    Simon Brown
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Indexed
public class BlogEntry {

  @Id @Length(min=1, max=20)
  @DocumentId
  private String id;
  
  @NotNull @Length(max=70)
  @Field(index = Index.TOKENIZED)
  private String title;
  
  @Length(max=200)
  private String excerpt;
  
  @NotNull @Length(max=1400)
  @Field(index = Index.TOKENIZED)
  private String body;
  
  @NotNull
  private Date date = new Date();
  
  @ManyToOne @NotNull 
  private Blog blog;

  public BlogEntry(Blog blog) {
    this.blog = blog;
  }
  
  BlogEntry() {}

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getExcerpt() {
    return excerpt;
  }

  public String getBody() {
    return body;
  }

  public Date getDate() {
    return date;
  }

   public void setBody(String body)
   {
      this.body = body;
   }
   
   public void setDate(Date date)
   {
      this.date = date;
   }
   
   public void setExcerpt(String excerpt)
   {
      if ( "".equals(excerpt) ) excerpt=null;
      this.excerpt = excerpt;
   }
   
   public void setId(String id)
   {
      this.id = id;
   }
   
   public void setTitle(String title)
   {
      this.title = title;
   }

}
