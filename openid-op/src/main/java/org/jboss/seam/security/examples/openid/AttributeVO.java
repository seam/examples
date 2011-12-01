package org.jboss.seam.security.examples.openid;

import org.jboss.seam.security.external.openid.api.OpenIdRequestedAttribute;

public class AttributeVO {
    private OpenIdRequestedAttribute requestedAttribute;

    private String attributeValue;

    public OpenIdRequestedAttribute getRequestedAttribute() {
        return requestedAttribute;
    }

    public void setRequestedAttribute(OpenIdRequestedAttribute requestedAttribute) {
        this.requestedAttribute = requestedAttribute;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }
}
