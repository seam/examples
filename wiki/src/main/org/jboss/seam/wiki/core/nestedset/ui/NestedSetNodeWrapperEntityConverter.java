package org.jboss.seam.wiki.core.nestedset.ui;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.ui.EntityConverter;
import org.jboss.seam.wiki.core.nestedset.query.NestedSetNodeWrapper;
import org.jboss.seam.wiki.core.nestedset.NestedSetNode;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.convert.ConverterException;

/**
 * Unwraps a wrapped Nested Set node and stores its identifier in the page context.
 * <p>
 * Because some clown in the JSF EG decided that a converter needs to return an instance
 * from <tt>getAsObject()</tt> that is <tt>equal()</tt> to one of the instances in the
 * <tt>SelectItem</tt>s list, we now need to override <tt>equals()</tt> in
 * <tt>NestedSetNodeWrapper</tt> to trick it. No, this check does not add security, it just
 * makes JSF less flexible.
 *
 * @author Christian Bauer
 */
@Name("nestedSetNodeWrapperEntityConverter")
public class NestedSetNodeWrapperEntityConverter extends EntityConverter {

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object o) throws ConverterException {
        String result;
        if (o instanceof NestedSetNodeWrapper) {
            result = super.getAsString(facesContext, uiComponent, ((NestedSetNodeWrapper)o).getWrappedNode());
            return result;
        } else {
            throw new IllegalArgumentException("Can not convert: " + o);
        }
    }

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) throws ConverterException {
        Object o = super.getAsObject(facesContext, uiComponent, s);
        return new NestedSetNodeWrapper( (NestedSetNode)o );
    }
}
