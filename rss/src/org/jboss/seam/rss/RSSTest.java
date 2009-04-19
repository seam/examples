package org.jboss.seam.rss;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("rss")
@Scope(ScopeType.SESSION)
public class RSSTest
{
   private Feed feed;

   @Create
   public void create()
   {
      feed = new Feed();
      List<Entry> entries = new ArrayList<Entry>();
      for (int i = 0; i < 5; i++)
      {
         Entry entry = new Entry();
         entry.setAuthor("Author " + i);
         entry.setLink("Link " + i);
         entry.setPublished(new Date(0));
         entry.setSummary("Summary <b>" + i + "</b>");
         entry.setTitle("Title <i>" + i + "</i>");
         entry.setUid(UUID.randomUUID().toString());
         entry.setUpdated(new Date());
         entries.add(entry);
      }
      feed.setEntries(entries);
      feed.setLink("Link Feed");
      feed.setSubtitle("Subtitle feed");
      feed.setTitle("Title Feed");
      feed.setUid(UUID.randomUUID().toString());
      feed.setUpdated(new Date());
   }

   public Feed getFeed()
   {
      return feed;
   }

}
