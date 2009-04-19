package org.jboss.seam.example.wicket;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.jboss.seam.wicket.ModelValidator;
import org.jboss.seam.wicket.SeamPropertyModel;

/**
 * Wicket allows you to build powerful custom components easily.
 * 
 * Here we've built generic border you can use to decorate a form input with 
 * a label, a * if the field is required and an feedback panel for displaying
 * any error messages.
 * 
 * It also attaches a model validator (which asks Seam to validate the input against
 * Hibernate Validator).
 * based
 * 
 * @author Pete Muir
 *
 */
public class FormInputBorder extends Border
{

   private FeedbackPanel feedbackPanel;
   private boolean ajaxValidate;
   private Model labelModel;
   
   /**
    * Create a new form input border which validates using ajax
    * @param id Id of border component on page
    * @param label Label to add
    * @param component The component to wrap
    * @param model The model to attach the component to
    */
   public FormInputBorder(String id, String label, FormComponent component, PropertyModel model, boolean ajaxValidate)
   {
      this(id, label, component, model, ajaxValidate, model.getTarget().getClass(), model.getPropertyExpression());
   }
   
   public FormInputBorder(String id, String label, FormComponent component, SeamPropertyModel model, boolean ajaxValidate)
   {
      this(id, label, component, model, ajaxValidate, model.getTarget().getClass(), model.getPropertyExpression());
   }
   
   public FormInputBorder(String id, String label, FormComponent component, PropertyModel model)
   {
      this(id, label, component, model, true, model.getTarget().getClass(), model.getPropertyExpression());
   }
   
   public FormInputBorder(String id, String label, FormComponent component, SeamPropertyModel model)
   {
      this(id, label, component, model, true, model.getTarget().getClass(), model.getPropertyExpression());
   }
   
   /**
    * Create a new form input border which validates
    * @param id Id of border component on page
    * @param label Label to add
    * @param component The component to wrap
    * @param model The model to attach the component to
    * @param ajaxValidate Whether to use ajax validation
    */
   public FormInputBorder(String id, String label, FormComponent component, IModel model, boolean ajaxValidate, Class modelClass, String propertyExpression)
   {
      super(id);
      this.ajaxValidate = ajaxValidate;
      labelModel = new Model(label);
      component.setLabel(labelModel);
      if (component.isRequired())
      {
         label += ":*";
      }
      else
      {
         label += ":";
      }
      Label labelComponent = new Label("label", label);
      add(labelComponent);
      feedbackPanel = new FeedbackPanel("message", new ContainerFeedbackMessageFilter(this));
      add(component, model, modelClass, propertyExpression);
      add(feedbackPanel);
   }
   
   public FormInputBorder add(FormComponent component, PropertyModel model)
   {
      return add(component, model, model.getTarget().getClass(), model.getPropertyExpression());
   }
   
   public FormInputBorder add(FormComponent component, IModel model, Class modelClass, String expression)
   {
      component.add(new ModelValidator(modelClass, expression));
      component.setModel(model);
      component.setLabel(labelModel);
      add(component);
      if (ajaxValidate)
      {
         feedbackPanel.setOutputMarkupId(true);
         component.add(new AjaxFormComponentUpdatingBehavior("onblur")
         {
   
            @Override
            protected void onUpdate(AjaxRequestTarget target)
            {
               getFormComponent().validate();
               target.addComponent(feedbackPanel);
            }
            
            @Override
            protected void onError(AjaxRequestTarget target, RuntimeException e)
            {
               target.addComponent(feedbackPanel);
            }
            
            @Override
            protected boolean getUpdateModel()
            {
               return true;
            }
            
         });
      }
      return this;
   }

}
