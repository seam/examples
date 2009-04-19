package org.jboss.seam.example.seambay;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.security.Identity;

@Stateless
@Name("auctionService")
@WebService(name = "AuctionService", serviceName = "AuctionService")
public class AuctionService implements AuctionServiceRemote
{           
   @WebMethod
   public boolean login(String username, String password)
   {
      Identity.instance().setUsername(username);
      Identity.instance().setPassword(password);
      Identity.instance().login();
      return Identity.instance().isLoggedIn();
   }
   
   @WebMethod
   public boolean logout()
   {
      Identity.instance().logout();
      return !Identity.instance().isLoggedIn();
   }
   
   @WebMethod @Restrict("#{identity.loggedIn}")
   public Category[] listCategories()
   {
      CategoryAction catAction = (CategoryAction) Component.getInstance(
            CategoryAction.class, true);
      
      List<Category> categories = catAction.getAllCategories();
      
      return categories.toArray(new Category[categories.size()]);
   }
   
   @WebMethod
   public void createAuction(String title, String description, int categoryId)
   {
      AuctionAction action = getAuctionAction();      
      action.createAuction();
      action.setDetails(title, description, categoryId);
   }
   
   @WebMethod
   public Auction getNewAuctionDetails()
   {
      return getAuctionAction().getAuction();
   }
   
   @WebMethod
   public void updateAuctionDetails(String title, String description, int categoryId)
   {     
      getAuctionAction().setDetails(title, description, categoryId);
   }
   
   @WebMethod
   public void setAuctionDuration(int days)
   {
      getAuctionAction().setDuration(days);
   }
   
   @WebMethod
   public void setAuctionPrice(double price)
   {
      getAuctionAction().getAuction().setStartingPrice(price);
   }
   
   @WebMethod
   public void confirmAuction()
   {
      getAuctionAction().confirm();
   }
      
   private AuctionAction getAuctionAction()
   {
      return (AuctionAction) Component.getInstance(AuctionAction.class, true);
   }   
   
   @WebMethod
   public Auction[] findAuctions(String searchTerm)
   {
      AuctionSearchAction search = (AuctionSearchAction) Component.getInstance(
            AuctionSearchAction.class, true);
 
      search.setSearchTerm(searchTerm);
      search.queryAuctions();
            
      return search.getAuctions().toArray(new Auction[search.getAuctions().size()]);
   }   
}
