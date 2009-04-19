package org.jboss.seam.example.seambay.test;

import java.util.List;

import javax.faces.model.DataModel;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.example.seambay.Auction;
import org.jboss.seam.example.seambay.Category;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class AuctionTest extends SeamTest
{
   @Test
   public void testCreateAuction() throws Exception
   {
      new FacesRequest() 
      {        
         @Override
         protected void invokeApplication() throws Exception
         {
            setValue("#{identity.username}", "demo");
            setValue("#{identity.password}", "demo");
            invokeAction("#{identity.login}");
            assert getValue("#{identity.loggedIn}").equals(true);            
         }
      }.run();  
      
      String cid = new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            invokeAction("#{auctionAction.createAuction}");
         }
         
         @Override
         protected void renderResponse()
         {
            Auction auction = (Auction) getValue("#{auctionAction.auction}");
            assert auction != null;
         }
      }.run();
            
      new FacesRequest("/sell.xhtml", cid)
      {
         @Override 
         protected void updateModelValues() throws Exception
         {
            setValue("#{auctionAction.auction.title}", "A Widget");
         }
      }.run();
      
      
      new FacesRequest("/sell2.xhtml", cid)
      {
         @Override 
         protected void updateModelValues() throws Exception
         {
            List<Category> categories = (List<Category>) getValue("#{allCategories}");
            
            setValue("#{auctionAction.auction.category}", categories.get(0));
         }
      }.run();      
      
      new FacesRequest("/sell3.xhtml", cid)
      {
         @Override 
         protected void updateModelValues() throws Exception
         {
            setValue("#{auctionAction.duration}", 3);
            setValue("#{auctionAction.auction.startingPrice}", 100.0);
         }
         
      }.run();
      
      new FacesRequest("/sell5.xhtml", cid)
      {
         @Override 
         protected void updateModelValues() throws Exception
         {
            setValue("#{auctionAction.auction.description}", "foo");
         }         
      }.run();      
      
      new FacesRequest("/preview.xhtml", cid)
      {
         @Override 
         protected void invokeApplication() throws Exception
         {
            Auction auction = (Auction) getValue("#{auctionAction.auction}");
            invokeAction("#{auctionAction.confirm}");
            assert auction.getStatus() == Auction.STATUS_LIVE;
         }         
      }.run();
      
      new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            invokeAction("#{identity.logout}");
            assert getValue("#{identity.loggedIn}").equals(false);
         }         
      }.run();
   }
 
   @Test
   public void testBidding() throws Exception
   {
      new FacesRequest() 
      {        
         @Override
         protected void invokeApplication() throws Exception
         {
            setValue("#{identity.username}", "demo");
            setValue("#{identity.password}", "demo");
            invokeAction("#{identity.login}");
            assert getValue("#{identity.loggedIn}").equals(true);
         }
      }.run();
            
      new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            invokeAction("#{auctionAction.createAuction}");
            setValue("#{auctionAction.auction.title}", "BidTestZZZ");
            setValue("#{auctionAction.auction.startingPrice}", 1);         
            setValue("#{auctionAction.auction.description}", "bar");
            setValue("#{auctionAction.categoryId}", 1001);
            
            Auction auction = (Auction) getValue("#{auctionAction.auction}"); 

            assert auction.getStatus() == Auction.STATUS_UNLISTED;
            
            invokeAction("#{auctionAction.confirm}");
            
            assert auction.getStatus() == Auction.STATUS_LIVE;            
            assert auction.getHighBid() == null;
         }
      }.run();      
      
      new FacesRequest()
      {
         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{auctionSearch.searchTerm}", "BidTestZZZ");
         }
         
         @Override
         protected void invokeApplication() throws Exception
         {
            assert invokeAction("#{auctionSearch.queryAuctions}") == null;
         }
         
         @Override
         protected void renderResponse() throws Exception
         {
            DataModel auctions = (DataModel) Contexts.getSessionContext().get("auctions");
            assert auctions.getRowCount() == 1;
            Auction auction = ((Auction) auctions.getRowData()); 
            assert auction.getTitle().equals("BidTestZZZ");
            assert auction.getHighBid() == null;
         }
         
      }.run();
         
      new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            Contexts.getEventContext().set("auction", getValue("#{auctionSearch.auctions[0]}"));
            
            assert invokeAction("#{bidAction.placeBid}") == null;
            assert getValue("#{bidAction.outcome}").equals("required");
            Contexts.getEventContext().set("bidAmount", "5.00");
            
            assert invokeAction("#{bidAction.placeBid}") == null;
            assert getValue("#{bidAction.outcome}").equals("confirm");
            
            assert invokeAction("#{bidAction.confirmBid}").equals("success");
         }
      }.run();
      
      new FacesRequest()
      {
         @Override
         protected void invokeApplication() throws Exception
         {
            assert invokeAction("#{auctionSearch.queryAuctions}") == null;
         }
         
         protected void renderResponse() throws Exception
         {
            DataModel auctions = (DataModel) Contexts.getSessionContext().get("auctions");
            Auction auction = ((Auction) auctions.getRowData());
            assert auction.getHighBid() != null;
         }
      }.run();
      
      
   }
   
}
