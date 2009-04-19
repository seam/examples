package org.jboss.seam.example.remoting.chatroom;

import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.cache.CacheProvider;
import org.jboss.cache.CacheException;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

@Name("chatroomUsers")
@Scope(ScopeType.STATELESS)
public class ChatroomUsers
{
   @SuppressWarnings("unchecked")
   @In CacheProvider cacheProvider;
   
   @SuppressWarnings("unchecked")
   @Unwrap
   public Set<String> getUsers() throws CacheException
   {
      Set<String> userList = (Set<String>) cacheProvider.get("chatroom", "userList");
      if (userList==null) 
      {
         userList = new HashSet<String>();
         cacheProvider.put("chatroom", "userList", userList);
      }
      return userList;
   }

}
