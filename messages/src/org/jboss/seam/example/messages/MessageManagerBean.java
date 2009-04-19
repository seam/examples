//$Id$
package org.jboss.seam.example.messages;

import static javax.persistence.PersistenceContextType.EXTENDED;
import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;

@Stateful
@Scope(SESSION)
@Name("messageManager")
public class MessageManagerBean implements Serializable, MessageManager
{

   @DataModel
   private List<Message> messageList;
   
   @DataModelSelection
   @Out(required=false)
   private Message message;
   
   @PersistenceContext(type=EXTENDED)
   private EntityManager em;
   
   @Factory("messageList")
   public void findMessages()
   {
      messageList = em.createQuery("select msg from Message msg order by msg.datetime desc").getResultList();
   }
   
   public void select()
   {
      if (message!=null) message.setRead(true);
   }
   
   public void delete()
   {
      if (message!=null)
      {
         messageList.remove(message);
         em.remove(message);
         message=null;
      }
   }
   
   @Remove @Destroy
   public void destroy() {}

}
