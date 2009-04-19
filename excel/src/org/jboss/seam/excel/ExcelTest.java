package org.jboss.seam.excel;

import java.util.LinkedList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("excelTest")
@Scope(ScopeType.SESSION)
public class ExcelTest
{

   private List<Person> people = new LinkedList<Person>();
   private List<Person> result = new LinkedList<Person>();
   private List<List<Person>> repeat = new LinkedList<List<Person>>();
   private String searchWord;
   
   
   @Create
   public void create()
   {
      people.add(new Person("Pete Muir", "Red Hat Inc.", "Project Lead"));
      people.add(new Person("Gavin King", "Red Hat Inc.", "Project Founder"));
      people.add(new Person("Norman Richards", "Red Hat Inc.", "Seam Core"));
      people.add(new Person("Shane Bryzak", "Red Hat Inc.", "Seam Security, Seam Remoting"));
      people.add(new Person("Michael Yuan", "Individual", ""));
      people.add(new Person("Mike Youngstrom", "Individual", "Spring Integration"));
      people.add(new Person("Ales Justin", "Red Hat Inc.", "JBoss5/MC Integration"));
      people.add(new Person("Christian Bauer", "Red Hat Inc.", "Seam Wiki, REST and GWT integration"));
      people.add(new Person("Jay Balunas", "Red Hat Inc.", "Cross-platform testing"));
      people.add(new Person("Dan Allen", "Individual", "Seam-gen, Bug fixes"));
      people.add(new Person("Matt Drees", "Individual", "Seam Core"));
      people.add(new Person("Jacob Orshalick", "Focus IT Solutions LLC", ""));
      people.add(new Person("Nicklas Karlsson", "Individual", "Excel reporting"));
      people.add(new Person("Daniel Roth", "Individual", "Excel reporting"));
      people.add(new Person("Judy Guglielmin", "ICESoft Inc.", "ICEFaces integration"));
      people.add(new Person("Max Rydahl Andersen", "Red Hat Inc.", "Lead developer JBoss Tools, Hibernate Tools, Seam Tools"));
      people.add(new Person("Emmanuel Bernard", "Red Hat Inc.", "Lead developer Hibernate Annotations, EntityManager, Validator, Search"));
      repeat.add(people.subList(0, 5));
   }

   public List<List<Person>> getRepeat()
   {
      return repeat;
   }

   public void setRepeat(List<List<Person>> repeat)
   {
      this.repeat = repeat;
   }
   
   public void setSearchWord(String searchWord)
   {
      this.searchWord = searchWord;
   }

   public String getSearchWord()
   {
      return searchWord;
   }

   public List<Person> getPeople()
   {
      return people;
   }

   public List<Person> getResult()
   {
      return result;
   }

   public void search()
   {
      result = new LinkedList<Person>();
      for (Person person : people)
      {
         if (person.getName().toLowerCase().contains(searchWord.toLowerCase()))
            result.add(person);
      }
   }

   public void clear()
   {
      this.result = new LinkedList<Person>();
   }

   public class Person
   {
      String name;
      String company;
      String task;

      public Person(String name, String company, String task)
      {
         this.company = company;
         this.name = name;
         this.task = task;
      }

      public String getName()
      {
         return name;
      }

      public String getTask()
      {
         return task;
      }

      public String getCompany()
      {
         return company;
      }

   }

}
