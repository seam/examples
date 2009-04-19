package org.jboss.seam.example.restbay;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
public class Category implements Serializable
{
   private static final long serialVersionUID = 411989568594034566L;
   
   private Integer categoryId;
   private String name;
   private Category parent;
   
   @Id
   @GeneratedValue
   public Integer getCategoryId()
   {
      return categoryId;
   }
   
   public void setCategoryId(Integer categoryId)
   {
      this.categoryId = categoryId;
   }
   
   public String getName()
   {
      return name;
   }
   
   public void setName(String name)
   {
      this.name = name;
   }
   
   @ManyToOne
   @JoinColumn(name = "PARENT_CATEGORY_ID")
   public Category getParent()
   {
      return parent;
   }
   
   public void setParent(Category parent)
   {
      this.parent = parent;
   }
}
