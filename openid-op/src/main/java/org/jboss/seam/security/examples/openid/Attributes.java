package org.jboss.seam.security.examples.openid;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Model;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.security.external.dialogues.api.DialogueScoped;
import org.jboss.seam.security.external.openid.api.OpenIdProviderApi;
import org.jboss.seam.security.external.openid.api.OpenIdRequestedAttribute;

@Model
@DialogueScoped
public class Attributes implements Serializable {
    private static final long serialVersionUID = -6945192710223411921L;

    private List<AttributeVO> attributeVOs;

    @Inject
    private OpenIdProviderApi providerApi;

    public void setRequestedAttributes(List<OpenIdRequestedAttribute> requestedAttributes) {
        attributeVOs = new LinkedList<AttributeVO>();

        for (OpenIdRequestedAttribute requestedAttribute : requestedAttributes) {
            AttributeVO attributeVO = new AttributeVO();
            attributeVO.setRequestedAttribute(requestedAttribute);
            attributeVOs.add(attributeVO);
        }
    }

    public List<AttributeVO> getAttributeVOs() {
        return attributeVOs;
    }

    public void confirm() {
        Map<String, List<String>> attributeValues = new HashMap<String, List<String>>();
        for (AttributeVO attributeVO : attributeVOs) {
            if (attributeVO.getAttributeValue() != null) {
                attributeValues.put(attributeVO.getRequestedAttribute().getAlias(), Arrays.asList(attributeVO.getAttributeValue()));
            }
        }
        providerApi.setAttributes(attributeValues, (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse());
    }
}
