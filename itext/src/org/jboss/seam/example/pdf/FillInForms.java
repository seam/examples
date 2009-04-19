package org.jboss.seam.example.pdf;


import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("fillInForms")
@Scope(ScopeType.CONVERSATION)
public class FillInForms
{
   private String name;
   private String address;
   private String postalCode;
   private String email;
   private String[] programming;
   private String language;
   private String preferred;
   private List<String> knowledge;
   private List<SelectItem> programmingLanguages;
   private List<SelectItem> languages;

   public List<String> getKnowledge()
   {
      return knowledge;
   }

   public void setKnowledge(List<String> knowledge)
   {
      this.knowledge = knowledge;
   }

   public List<SelectItem> getLanguages()
   {
      return languages;
   }

   public void setLanguages(List<SelectItem> languages)
   {
      this.languages = languages;
   }

   public List<SelectItem> getProgrammingLanguages()
   {
      return programmingLanguages;
   }

   public void setProgrammingLanguages(List<SelectItem> programmingLanguages)
   {
      this.programmingLanguages = programmingLanguages;
   }

   @Create
   public void init()
   {
      knowledge = new ArrayList<String>();
      programmingLanguages = new ArrayList<SelectItem>();
      programmingLanguages.add(new SelectItem("JAVA", "Java"));
      programmingLanguages.add(new SelectItem("C", "C/C++"));
      programmingLanguages.add(new SelectItem("CS", "C#"));
      programmingLanguages.add(new SelectItem("VB", "VB"));
      languages = new ArrayList<SelectItem>();
      languages.add(new SelectItem("EN", "English"));
      languages.add(new SelectItem("FR", "French"));
      languages.add(new SelectItem("NL", "Dutch"));
      knowledge.add("FR");
      language = "FR";
      preferred = "FR";
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getAddress()
   {
      return address;
   }

   public void setAddress(String address)
   {
      this.address = address;
   }

   public String getPostalCode()
   {
      return postalCode;
   }

   public void setPostalCode(String postalCode)
   {
      this.postalCode = postalCode;
   }

   public String getEmail()
   {
      return email;
   }

   public void setEmail(String email)
   {
      this.email = email;
   }

   public String[] getProgramming()
   {
      return programming;
   }

   public void setProgramming(String[] programming)
   {
      this.programming = programming;
   }

   public String getLanguage()
   {
      return language;
   }

   public void setLanguage(String language)
   {
      this.language = language;
   }

   public String getPreferred()
   {
      return preferred;
   }

   public void setPreferred(String preferred)
   {
      this.preferred = preferred;
   }

   public String submit()
   {
      return "/form.xhtml";
   }

   public boolean isKnowsEnglish()
   {
      boolean knows = knowledge.contains("EN");
      return knows;
   }

   public boolean isKnowsFrench()
   {
      return knowledge.contains("FR");
   }

   public boolean isKnowsDutch()
   {
      return knowledge.contains("NL");
   }

}
