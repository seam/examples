package org.jboss.seam.example.seambay;

import javax.ejb.Remote;

@Remote
public interface AuctionServiceRemote
{
   boolean login(String username, String password);
   boolean logout();
   
   Category[] listCategories();
   
   void createAuction(String title, String description, int categoryId);   
   Auction getNewAuctionDetails();
   void updateAuctionDetails(String title, String description, int categoryId);
   void setAuctionDuration(int days);
   void setAuctionPrice(double price);
   void confirmAuction();
   
   Auction[] findAuctions(String searchTerm);
}
