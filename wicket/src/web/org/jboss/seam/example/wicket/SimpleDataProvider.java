package org.jboss.seam.example.wicket;

import java.io.Serializable;

import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public abstract class SimpleDataProvider implements IDataProvider
{

   public IModel model(Object object)
   {
      return new Model((Serializable) object);
   }

   public void detach()
   {
      // No - op
   }

}
