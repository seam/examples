package org.jboss.seam.rss;

import java.util.Date;

public class Entry
{
   private String uid;
   private String title;
   private String link;
   private String author;
   private String summary;
   private Date published;
   private Date updated;

   public String getUid()
   {
      return uid;
   }

   public void setUid(String uid)
   {
      this.uid = uid;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public String getLink()
   {
      return link;
   }

   public void setLink(String link)
   {
      this.link = link;
   }

   public String getAuthor()
   {
      return author;
   }

   public void setAuthor(String author)
   {
      this.author = author;
   }

   public String getSummary()
   {
      return summary;
   }

   public void setSummary(String summary)
   {
      this.summary = summary;
   }

   public Date getPublished()
   {
      return published;
   }

   public void setPublished(Date published)
   {
      this.published = published;
   }

   public Date getUpdated()
   {
      return updated;
   }

   public void setUpdated(Date updated)
   {
      this.updated = updated;
   }
}
