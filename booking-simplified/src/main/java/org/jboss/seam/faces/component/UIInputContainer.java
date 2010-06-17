/* 
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.faces.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.ValueReference;
import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIInput;
import javax.faces.component.UIMessage;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIViewRoot;
import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.context.FacesContext;
import javax.faces.validator.BeanValidator;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.PropertyDescriptor;

/**
 * <strong>UIInputContainer</strong> is a supplemental component for a JSF 2.0
 * composite component encapsulating one or more input components
 * (<strong>EditableValueHolder</strong>), their corresponding message
 * components (<strong>UIMessage</strong>) and a label
 * (<strong>HtmlOutputLabel</strong>). This component takes care of wiring the
 * label to the first input and the messages to each input in sequence. It also
 * assigns two implicit attribute values, "required" and "invalid" to indicate
 * that a required input field is present and whether there are any validation
 * errors, respectively. To determine if a input field is required, both the
 * required attribute is consulted and whether the property has Bean Validation
 * constraints. Finally, if the "label" attribute is not provided on the
 * composite component, the label value will be derived from the id of the
 * composite component, for convenience.
 * 
 * <p>
 * Composite component definition example (minus layout):
 * </p>
 * 
 * <pre>
 * &lt;cc:interface componentType="org.jboss.seam.faces.InputContainer"/>
 * &lt;cc:implementation>
 *   &lt;h:outputLabel id="label" value="#{cc.attrs.label}:" styleClass="#{cc.attrs.invalid ? 'invalid' : ''}">
 *     &lt;h:ouputText styleClass="required" rendered="#{cc.attrs.required}" value="*"/>
 *   &lt;/h:outputLabel>
 *   &lt;cc:insertChildren/>
 *   &lt;h:message id="message" errorClass="invalid message" rendered="#{cc.attrs.invalid}"/>
 * &lt;/cc:implementation>
 * </pre>
 * 
 * <p>
 * Composite component usage example:
 * </p>
 * 
 * <pre>
 * &lt;example:inputContainer id="name">
 *   &lt;h:inputText id="input" value="#{person.name}"/>
 * &lt;/example:inputContainer>
 * </pre>
 * 
 * <p>
 * Possible enhancements:
 * </p>
 * <ul>
 * <li>append styleClass "invalid" to label, inputs and messages when invalid</li>
 * </ul>
 * 
 * <p>
 * NOTE: Firefox does not properly associate a label with the target input if
 * the input id contains a colon (:), the default separator character in JSF.
 * JSF 2 allows developers to set the value via an initialization parameter
 * (context-param in web.xml) keyed to javax.faces.SEPARATOR_CHAR. We recommend
 * that you override this setting to make the separator an underscore (_).
 * </p>
 * 
 * @author Dan Allen
 */
@FacesComponent(UIInputContainer.COMPONENT_TYPE)
public class UIInputContainer extends UIComponentBase implements NamingContainer
{
   /**
    * The standard component type for this component.
    */
   public static final String COMPONENT_TYPE = "org.jboss.seam.faces.InputContainer";

   protected static final String HTML_ID_ATTR_NAME = "id";
   protected static final String HTML_CLASS_ATTR_NAME = "class";
   protected static final String HTML_STYLE_ATTR_NAME = "style";

   private boolean beanValidationPresent = false;

   public UIInputContainer()
   {
      beanValidationPresent = isClassPresent("javax.validation.Validator");
   }

   @Override
   public String getFamily()
   {
      return UINamingContainer.COMPONENT_FAMILY;
   }

   /**
    * The name of the auto-generated composite component attribute that holds a
    * boolean indicating whether the the template contains an invalid input.
    */
   public String getInvalidAttributeName()
   {
      return "invalid";
   }

   /**
    * The name of the auto-generated composite component attribute that holds a
    * boolean indicating whether the template contains a required input.
    */
   public String getRequiredAttributeName()
   {
      return "required";
   }

   /**
    * The name of the composite component attribute that holds the string label
    * for this set of inputs. If the label attribute is not provided, one will
    * be generated from the id of the composite component or, if the id is
    * defaulted, the name of the property bound to the first input.
    */
   public String getLabelAttributeName()
   {
      return "label";
   }

   /**
    * The name of the auto-generated composite component attribute that holds
    * the elements in this input container. The elements include the label, a
    * list of inputs and a cooresponding list of messages.
    */
   public String getElementsAttributeName()
   {
      return "elements";
   }

   /**
    * The name of the composite component attribute that holds a boolean
    * indicating whether the component template should be enclosed in an HTML
    * element, so that it be referenced from JavaScript.
    */
   public String getEncloseAttributeName()
   {
      return "enclose";
   }

   /**
    * The name of the composite component attribute that indicates whether
    * the AjaxBehavior should be added to inputs in this container and
    * which event to fire on. If the value is default, then the default
    * event for the input component will be used.
    */
   public String getAjaxAttributeName()
   {
      return "ajax";
   }

   public String getContainerElementName()
   {
      return "div";
   }

   public String getDefaultLabelId()
   {
      return "label";
   }

   public String getDefaultInputId()
   {
      return "input";
   }

   public String getDefaultMessageId()
   {
      return "message";
   }

   private InputContainerElements elements;

   @Override
   public void encodeBegin(final FacesContext context) throws IOException
   {
      if (!isRendered())
      {
         return;
      }

      super.encodeBegin(context);

      elements = scan(getFacet(UIComponent.COMPOSITE_FACET_NAME), context);
      // assignIds(elements, context);
      wire(elements, context);

      getAttributes().put(getElementsAttributeName(), elements);

      getAttributes().put(getInvalidAttributeName(), elements.hasValidationError());

      // set the required attribute, but only if the user didn't already assign it
      if (!getAttributes().containsKey(getRequiredAttributeName()) && elements.hasRequiredInput())
      {
         getAttributes().put(getRequiredAttributeName(), true);
      }

      if (!getAttributes().containsKey(getLabelAttributeName()))
      {
         getAttributes().put(getLabelAttributeName(), generateLabel(elements, context));
      }

      if (Boolean.TRUE.equals(getAttributes().get(getEncloseAttributeName())))
      {
         startContainerElement(context);
      }
   }

   @Override
   public void encodeEnd(final FacesContext context) throws IOException
   {
      if (!isRendered())
      {
         return;
      }

      super.encodeEnd(context);

      if (Boolean.TRUE.equals(getAttributes().get(getEncloseAttributeName())))
      {
         endContainerElement(context);
      }

      // temporary workaround for state saving bug; remove any ClientBehaviors added dynamically
      for (EditableValueHolder i : elements.getInputs())
      {
         if (i instanceof ClientBehaviorHolder)
         {
            Map<String, List<ClientBehavior>> b = ((ClientBehaviorHolder) i).getClientBehaviors();
            for (String k : b.keySet())
            {
               b.get(k).clear();
            }
         }
      }
   }

   protected void startContainerElement(final FacesContext context) throws IOException
   {
      context.getResponseWriter().startElement(getContainerElementName(), this);
      String style = (getAttributes().get("style") != null ? getAttributes().get("style").toString().trim() : null);
      if (style.length() > 0)
      {
         context.getResponseWriter().writeAttribute(HTML_STYLE_ATTR_NAME, style, HTML_STYLE_ATTR_NAME);
      }
      String styleClass = (getAttributes().get("styleClass") != null ? getAttributes().get("styleClass").toString().trim() : null);
      if (styleClass.length() > 0)
      {
         context.getResponseWriter().writeAttribute(HTML_CLASS_ATTR_NAME, styleClass, HTML_CLASS_ATTR_NAME);
      }
      context.getResponseWriter().writeAttribute(HTML_ID_ATTR_NAME, getClientId(context), HTML_ID_ATTR_NAME);
   }

   protected void endContainerElement(final FacesContext context) throws IOException
   {
      context.getResponseWriter().endElement(getContainerElementName());
   }

   protected String generateLabel(final InputContainerElements elements, final FacesContext context)
   {
      String name = getId().startsWith(UIViewRoot.UNIQUE_ID_PREFIX) ? elements.getPropertyName(context) : getId();
      StringBuilder builder = new StringBuilder(name.length());
      boolean first = true;
      for (char c : name.toCharArray())
      {
         if (first)
         {
            builder.append(Character.toUpperCase(c));
            first = false;
         }
         else if (Character.isUpperCase(c))
         {
            builder.append(" ");
            builder.append(Character.toLowerCase(c));
         }
         else
         {
            builder.append(c);
         }
      }
      return builder.toString();
   }

   /**
    * Walk the component tree branch built by the composite component and locate
    * the input container elements.
    * 
    * @return a composite object of the input container elements
    */
   protected InputContainerElements scan(final UIComponent component, final FacesContext context)
   {
      if (elements == null)
      {
         elements = new InputContainerElements(getId(), getAttributes());
      }

      // NOTE we need to walk the tree ignoring rendered attribute because it's
      // condition
      // could be based on what we discover
      if ((elements.getLabel() == null) && (component instanceof HtmlOutputLabel))
      {
         elements.setLabel((HtmlOutputLabel) component);
      }
      else if (component instanceof EditableValueHolder)
      {
         elements.registerInput((EditableValueHolder) component, getDefaultValidator(context), context);
      }
      else if (component instanceof UIMessage)
      {
         elements.registerMessage((UIMessage) component);
      }
      // may need to walk smarter to ensure "element of least suprise"
      for (UIComponent child : component.getChildren())
      {
         scan(child, context);
      }
      return elements;
   }

   // assigning ids seems to break form submissions, but I don't know why
   public void assignIds(final InputContainerElements elements, final FacesContext context)
   {
      boolean refreshIds = false;
      if (getId().startsWith(UIViewRoot.UNIQUE_ID_PREFIX))
      {
         setId(elements.getPropertyName(context));
         refreshIds = true;
      }
      UIComponent label = elements.getLabel();
      if (label != null)
      {
         if (label.getId().startsWith(UIViewRoot.UNIQUE_ID_PREFIX))
         {
            label.setId(getDefaultLabelId());
         }
         else if (refreshIds)
         {
            label.setId(label.getId());
         }
      }
      for (int i = 0, len = elements.getInputs().size(); i < len; i++)
      {
         UIComponent input = (UIComponent) elements.getInputs().get(i);
         if (input.getId().startsWith(UIViewRoot.UNIQUE_ID_PREFIX))
         {
            input.setId(getDefaultInputId() + (i == 0 ? "" : (i + 1)));
         }
         else if (refreshIds)
         {
            input.setId(input.getId());
         }
      }
      for (int i = 0, len = elements.getMessages().size(); i < len; i++)
      {
         UIComponent msg = elements.getMessages().get(i);
         if (msg.getId().startsWith(UIViewRoot.UNIQUE_ID_PREFIX))
         {
            msg.setId(getDefaultMessageId() + (i == 0 ? "" : (i + 1)));
         }
         else if (refreshIds)
         {
            msg.setId(msg.getId());
         }
      }
   }

   /**
    * Wire the label and messages to the input(s)
    */
   protected void wire(final InputContainerElements elements, final FacesContext context)
   {
      elements.wire(context);
   }

   /**
    * Get the default Bean Validation Validator to read the contraints for a
    * property.
    */
   private Validator getDefaultValidator(final FacesContext context) throws FacesException
   {
      if (!beanValidationPresent)
      {
         return null;
      }

      ValidatorFactory validatorFactory;
      Object cachedObject = context.getExternalContext().getApplicationMap().get(BeanValidator.VALIDATOR_FACTORY_KEY);
      if (cachedObject instanceof ValidatorFactory)
      {
         validatorFactory = (ValidatorFactory) cachedObject;
      }
      else
      {
         try
         {
            validatorFactory = Validation.buildDefaultValidatorFactory();
         }
         catch (ValidationException e)
         {
            throw new FacesException("Could not build a default Bean Validator factory", e);
         }
         context.getExternalContext().getApplicationMap().put(BeanValidator.VALIDATOR_FACTORY_KEY, validatorFactory);
      }
      return validatorFactory.getValidator();
   }

   private boolean isClassPresent(final String fqcn)
   {
      try
      {
         if (Thread.currentThread().getContextClassLoader() != null)
         {
            return Thread.currentThread().getContextClassLoader().loadClass(fqcn) != null;
         }
         else
         {
            return Class.forName(fqcn) != null;
         }
      }
      catch (ClassNotFoundException e)
      {
         return false;
      }
      catch (NoClassDefFoundError e)
      {
         return false;
      }
   }

   public class InputContainerElements
   {
      private String containerId;
      private Map<String, Object> attributes;
      private String propertyName;
      private HtmlOutputLabel label;
      private final List<EditableValueHolder> inputs = new ArrayList<EditableValueHolder>();
      private final List<UIMessage> messages = new ArrayList<UIMessage>();
      private boolean validationError = false;
      private boolean requiredInput = false;

      public InputContainerElements(final String containerId, final Map<String, Object> attributes)
      {
         this.containerId = containerId;
         this.attributes = attributes;
      }

      public HtmlOutputLabel getLabel()
      {
         return label;
      }

      public void setLabel(final HtmlOutputLabel label)
      {
         this.label = label;
      }

      public List<EditableValueHolder> getInputs()
      {
         return inputs;
      }

      public void registerInput(final EditableValueHolder input, final Validator validator, final FacesContext context)
      {
         inputs.add(input);
         if (input instanceof ClientBehaviorHolder)
         {
            ClientBehaviorHolder bh = (ClientBehaviorHolder) input;
            String ajaxEvent = (String) attributes.get(getAjaxAttributeName());
            if (ajaxEvent != null && (ajaxEvent.equalsIgnoreCase("default")))
            {
               ajaxEvent = bh.getDefaultEventName();
            }
            if (ajaxEvent != null)
            {
               Map<String, List<ClientBehavior>> behaviors = bh.getClientBehaviors();
               if (!behaviors.containsKey(ajaxEvent))
               {
                  AjaxBehavior ajax = new AjaxBehavior();
                  ajax.setRender(Arrays.asList(containerId));
                  bh.addClientBehavior(ajaxEvent.toLowerCase(), ajax);
               }
            }
         }
         if (input.isRequired() || isRequiredByConstraint(input, validator, context))
         {
            requiredInput = true;
         }
         if (!input.isValid())
         {
            validationError = true;
         }
         // optimization to avoid loop if already flagged
         else if (!validationError)
         {
            Iterator<FacesMessage> it = context.getMessages(((UIComponent) input).getClientId(context));
            while (it.hasNext())
            {
               if (it.next().getSeverity().compareTo(FacesMessage.SEVERITY_WARN) >= 0)
               {
                  validationError = true;
                  break;
               }
            }
         }
      }

      public List<UIMessage> getMessages()
      {
         return messages;
      }

      public void registerMessage(final UIMessage message)
      {
         messages.add(message);
      }

      public boolean hasValidationError()
      {
         return validationError;
      }

      public boolean hasRequiredInput()
      {
         return requiredInput;
      }

      private boolean isRequiredByConstraint(final EditableValueHolder input, final Validator validator, final FacesContext context)
      {
         if (validator == null)
         {
            return false;
         }

         ValueReference vref = buildValueReference(input, context);
         String property = (String) vref.getProperty();
         // optimization, though not sitting right
         if (propertyName != null)
         {
            propertyName = property;
         }
         PropertyDescriptor d = validator.getConstraintsForClass(vref.getBase().getClass()).getConstraintsForProperty(property);
         return (d != null) && d.hasConstraints();
      }

      /**
       * Gets the name of the property bound to the first input, or null if there
       * are no inputs present.
       */
      public String getPropertyName(final FacesContext context)
      {
         if (propertyName != null)
         {
            return propertyName;
         }

         if (inputs.size() == 0)
         {
            return null;
         }

         propertyName = buildValueReference(inputs.get(0), context).getProperty().toString();
         return propertyName;
      }

      private ValueReference buildValueReference(final EditableValueHolder input, final FacesContext context)
      {
         // NOTE believe it or not, getValueReference on ValueExpression is broken, so we have to do it ourselves
         return new ValueExpressionAnalyzer(((UIComponent) input).getValueExpression("value"))
            .getValueReference(context.getELContext());
      }

      public void wire(final FacesContext context)
      {
         int numInputs = inputs.size();
         if (numInputs > 0)
         {
            if (label != null)
            {
               label.setFor(((UIComponent) inputs.get(0)).getClientId(context));
            }
            for (int i = 0, len = messages.size(); i < len; i++)
            {
               if (i < numInputs)
               {
                  messages.get(i).setFor(((UIComponent) inputs.get(i)).getClientId(context));
               }
            }
         }
      }
   }
}
