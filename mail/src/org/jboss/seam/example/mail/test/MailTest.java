package org.jboss.seam.example.mail.test;

import static javax.mail.Message.RecipientType.BCC;
import static javax.mail.Message.RecipientType.CC;

import java.io.InputStream;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.document.ByteArrayDocumentData;
import org.jboss.seam.document.DocumentData;
import org.jboss.seam.example.mail.Person;
import org.jboss.seam.mail.MailSession;
import org.jboss.seam.mail.ui.UIAttachment;
import org.jboss.seam.mail.ui.UIMessage;
import org.jboss.seam.mock.MockTransport;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

/**
 * @author Pete Muir
 *
 */
public class MailTest extends SeamTest
{
    
    @Test
    public void testSimple() throws Exception
    {
        
        new FacesRequest()
        {

            @Override
            protected void updateModelValues() throws Exception
            {
                setValue("#{person.firstname}", "Pete");
                setValue("#{person.lastname}", "Muir");
                setValue("#{person.address}", "test@example.com");
            }
            
            @Override
            protected void invokeApplication() throws Exception
            {
                MimeMessage renderedMessage = getRenderedMailMessage("/simple.xhtml");
             
                assert MailSession.instance().getTransport() instanceof MockTransport;
                
                // Test the headers
                
                assert renderedMessage != null;
                assert renderedMessage.getAllRecipients().length == 1;
                assert renderedMessage.getAllRecipients()[0] instanceof InternetAddress;
                InternetAddress to = (InternetAddress) renderedMessage.getAllRecipients()[0];
                assert to.getAddress().equals("test@example.com");
                assert to.getPersonal().equals("Pete Muir");
                assert renderedMessage.getFrom().length == 1;
                assert renderedMessage.getFrom()[0] instanceof InternetAddress;
                InternetAddress from = (InternetAddress) renderedMessage.getFrom()[0];
                assert from.getAddress().equals("peter@example.com");
                assert from.getPersonal().equals("Peter");
                assert "Try out Seam!".equals(renderedMessage.getSubject());
                assert renderedMessage.getHeader("Precedence") == null;
                assert renderedMessage.getHeader("X-Priority") == null;
                assert renderedMessage.getHeader("Priority") == null;
                assert renderedMessage.getHeader("Importance") == null;
                assert renderedMessage.getHeader("Disposition-Notification-To") == null;

                
                // Check the body
                
                assert renderedMessage.getContent() != null;
                assert renderedMessage.getContent() instanceof MimeMultipart;
                MimeMultipart body = (MimeMultipart) renderedMessage.getContent();
                assert body.getCount() == 1;
                assert body.getBodyPart(0) != null;
                assert body.getBodyPart(0) instanceof MimeBodyPart;
                MimeBodyPart bodyPart = (MimeBodyPart) body.getBodyPart(0);
                assert bodyPart.getContent() != null;
                assert "inline".equals(bodyPart.getDisposition());
                assert bodyPart.isMimeType("text/html");

            }            
        }.run();
       
    }
    
    
    
    @Test
    public void testAttachment() throws Exception
    {
        
        new FacesRequest()
        {

            @Override
            protected void updateModelValues() throws Exception
            {
                setValue("#{person.firstname}", "Gavin");
                setValue("#{person.lastname}", "King");
                setValue("#{person.address}", "gavin@king.com");
            }
            
            @Override
            protected void invokeApplication() throws Exception
            { 
                MimeMessage renderedMessage = getRenderedMailMessage("/attachment.xhtml");
                
                // Test the headers
                
                InternetAddress to = (InternetAddress) renderedMessage.getAllRecipients()[0];
                assert to.getAddress().equals("gavin@king.com");
                assert to.getPersonal().equals("Gavin King");
                InternetAddress from = (InternetAddress) renderedMessage.getFrom()[0];
                assert from.getAddress().equals("do-not-reply@jboss.com");
                assert from.getPersonal().equals("Seam");
                assert "Try out Seam!".equals(renderedMessage.getSubject());
                MimeMultipart body = (MimeMultipart) renderedMessage.getContent();
                
                // Test the attachments (no ui:repeat atm, so only 6)
                assert body.getCount() == 1;
                
                // The root multipart/related
                assert body.getBodyPart(0) != null;
                assert body.getBodyPart(0) instanceof MimeBodyPart;
                MimeBodyPart bodyPart = (MimeBodyPart) body.getBodyPart(0);
                assert bodyPart.getContent() != null;
                assert bodyPart.getContent() instanceof MimeMultipart;
                assert bodyPart.isMimeType("multipart/related");
                
                MimeMultipart attachments = (MimeMultipart) bodyPart.getContent();
                
                // Attachment 0 (the actual message)
                assert attachments.getBodyPart(0) != null;                
                assert attachments.getBodyPart(0) instanceof MimeBodyPart;
                bodyPart = (MimeBodyPart) attachments.getBodyPart(0);
                assert bodyPart.getContent() != null;
                assert bodyPart.getContent() != null;
                assert "inline".equals(bodyPart.getDisposition());
                assert bodyPart.isMimeType("text/html");
                
                // Attachment 1
                assert attachments.getBodyPart(1) != null;                
                assert attachments.getBodyPart(1) instanceof MimeBodyPart;
                bodyPart = (MimeBodyPart) attachments.getBodyPart(1);
                assert bodyPart.getContent() != null;
                assert bodyPart.getContent() instanceof InputStream;
                assert "jboss.jpg".equals(bodyPart.getFileName());
                assert bodyPart.isMimeType("image/jpeg");
                assert "inline".equals(bodyPart.getDisposition());
                
                // Attachment 2
                assert attachments.getBodyPart(2) != null;                
                assert attachments.getBodyPart(2) instanceof MimeBodyPart;
                bodyPart = (MimeBodyPart) attachments.getBodyPart(2);
                assert bodyPart.getContent() != null;
                assert bodyPart.getContent() instanceof InputStream;
                assert "numbers.csv".equals(bodyPart.getFileName());
                assert bodyPart.isMimeType("content/unknown");
                assert "attachment".equals(bodyPart.getDisposition());
                
                // Attachment 3
                assert attachments.getBodyPart(3) != null;                
                assert attachments.getBodyPart(3) instanceof MimeBodyPart;
                bodyPart = (MimeBodyPart) attachments.getBodyPart(3);
                assert bodyPart.getContent() != null;
                assert bodyPart.getContent() != null;
                assert bodyPart.getContent() instanceof InputStream;
                assert "Gavin_King.jpg".equals(bodyPart.getFileName());
                assert bodyPart.isMimeType("image/png");
                assert "inline".equals(bodyPart.getDisposition());
                
                // Attachment 4
                assert attachments.getBodyPart(4) != null;                
                assert attachments.getBodyPart(4) instanceof MimeBodyPart;
                bodyPart = (MimeBodyPart) attachments.getBodyPart(4);
                assert bodyPart.getContent() != null;
                // No PDF rendering here :(
                assert bodyPart.getContent() instanceof String;
                assert "whyseam.pdf".equals(bodyPart.getFileName());
                assert "attachment".equals(bodyPart.getDisposition());
                
                // Attachment 5 -- ui:repeat doesn't work in test env :(
                /*assert attachments.getBodyPart(5) != null;                
                assert attachments.getBodyPart(5) instanceof MimeBodyPart;
                bodyPart = (MimeBodyPart) attachments.getBodyPart(5);
                assert bodyPart.getContent() != null;
                assert "Gavin_King.jpg".equals(bodyPart.getFileName());
                assert bodyPart.isMimeType("image/jpeg");
                assert "attachment".equals(bodyPart.getDisposition());*/
                
            }            
        }.run();
       
    }
    
    /**
     * This test is needed since the PDF is not rendered in the attachment test.
     * If PDF rendering is supported in a test environment, then this test can be
     * removed.
     */
    @Test
    public void testPdfAttachment() throws Exception
    {
       new FacesRequest()
       {
          @Override
          protected void invokeApplication() throws Exception
          {
             UIAttachment attachment = new UIAttachment();
             attachment.setFileName("filename.pdf");
             UIMessage message = new UIMessage();
             attachment.setParent(message);
             message.setMailSession(MailSession.instance());
             DocumentData doc = new ByteArrayDocumentData("filename", new DocumentData.DocumentType("pdf", "application/pdf"), new byte[] {});
             attachment.setValue(doc);
             attachment.encodeEnd(FacesContext.getCurrentInstance());
             
             // verify we built the message
             assert new Integer(1).equals(message.getAttachments().size());
             MimeBodyPart bodyPart = message.getAttachments().get(0);
             assert "filename.pdf".equals(bodyPart.getFileName());
             assert "attachment".equals(bodyPart.getDisposition());
          }
       }.run();
    }
    
    @Test
    public void testHtml() throws Exception
    {
        
        new FacesRequest()
        {

            @Override
            protected void updateModelValues() throws Exception
            {
                setValue("#{person.firstname}", "Pete");
                setValue("#{person.lastname}", "Muir");
                setValue("#{person.address}", "test@example.com");
            }
            
            @Override
            protected void invokeApplication() throws Exception
            {   
                MimeMessage renderedMessage = getRenderedMailMessage("/html.xhtml");
             
                // Test the standard headers
                
                InternetAddress to = (InternetAddress) renderedMessage.getAllRecipients()[0];
                assert to.getAddress().equals("test@example.com");
                assert to.getPersonal().equals("Pete Muir");
                InternetAddress from = (InternetAddress) renderedMessage.getFrom()[0];
                assert from.getAddress().equals("do-not-reply@jboss.com");
                assert from.getPersonal().equals("Seam");
                assert "Seam Mail".equals(renderedMessage.getSubject());
                
                // Test the extra headers
                
                // Importance
                assert renderedMessage.getHeader("X-Priority") != null;
                assert renderedMessage.getHeader("Priority") != null;
                assert renderedMessage.getHeader("Importance") != null;
                assert renderedMessage.getHeader("X-Priority").length == 1;
                assert renderedMessage.getHeader("Priority").length == 1;
                assert renderedMessage.getHeader("Importance").length == 1;
                assert "5".equals(renderedMessage.getHeader("X-Priority")[0]);
                assert "Non-urgent".equals(renderedMessage.getHeader("Priority")[0]);
                assert "low".equals(renderedMessage.getHeader("Importance")[0]);
                
                // read receipt
                assert renderedMessage.getHeader("Disposition-Notification-To") != null;
                assert renderedMessage.getHeader("Disposition-Notification-To").length == 1;
                assert "Seam <do-not-reply@jboss.com>".equals(renderedMessage.getHeader("Disposition-Notification-To")[0]);
                
                // m:header
                assert renderedMessage.getHeader("X-Sent-From") != null;
                assert renderedMessage.getHeader("X-Sent-From").length == 1;
                assert "Seam".equals(renderedMessage.getHeader("X-Sent-From")[0]);
                
                MimeMultipart body = (MimeMultipart) renderedMessage.getContent();
                
                // Check the alternative facet
                assert renderedMessage.getContentType().startsWith("multipart/mixed");
                assert body.getCount() == 1;
                MimeBodyPart bodyPart = (MimeBodyPart) body.getBodyPart(0);
                assert bodyPart.getContentType().startsWith("multipart/alternative");
                assert bodyPart.getContent() instanceof MimeMultipart;
                MimeMultipart bodyParts = (MimeMultipart) bodyPart.getContent();
                assert bodyParts.getCount() == 2;
                assert bodyParts.getBodyPart(0) instanceof MimeBodyPart;
                assert bodyParts.getBodyPart(1) instanceof MimeBodyPart;
                MimeBodyPart alternative = (MimeBodyPart) bodyParts.getBodyPart(0);
                MimeBodyPart html = (MimeBodyPart) bodyParts.getBodyPart(1);
                assert alternative.isMimeType("text/plain");
                assert "inline".equals(alternative.getDisposition());
                assert html.isMimeType("text/html");
                assert "inline".equals(html.getDisposition());
            }            
        }.run();
       
    }
    
    
    @Test
    public void testPlain() throws Exception
    {
        
        new FacesRequest()
        {

            @Override
            protected void updateModelValues() throws Exception
            {
                setValue("#{person.firstname}", "Pete");
                setValue("#{person.lastname}", "Muir");
                setValue("#{person.address}", "test@example.com");
            }
            
            @Override
            protected void invokeApplication() throws Exception
            {   
                MimeMessage renderedMessage = getRenderedMailMessage("/plain.xhtml");
             
                // Test the standard headers
                
                InternetAddress to = (InternetAddress) renderedMessage.getAllRecipients()[0];
                assert to.getAddress().equals("test@example.com");
                assert to.getPersonal().equals("Pete Muir");
                InternetAddress from = (InternetAddress) renderedMessage.getFrom()[0];
                assert from.getAddress().equals("do-not-reply@jboss.com");
                assert from.getPersonal().equals("Seam");
                assert renderedMessage.getReplyTo().length == 1;
                assert renderedMessage.getReplyTo()[0] instanceof InternetAddress;
                InternetAddress replyTo = (InternetAddress) renderedMessage.getReplyTo()[0];
                assert "another.address@jboss.org".equals(replyTo.getAddress());
                assert "JBoss".equals(replyTo.getPersonal());
                assert renderedMessage.getRecipients(CC).length == 1;
                assert renderedMessage.getRecipients(CC)[0] instanceof InternetAddress;
                InternetAddress cc = (InternetAddress) renderedMessage.getRecipients(CC)[0];
                assert "test@example.com".equals(cc.getAddress());
                assert "Pete Muir".equals(cc.getPersonal());
                assert renderedMessage.getRecipients(BCC).length == 1;
                assert renderedMessage.getRecipients(BCC)[0] instanceof InternetAddress;
                InternetAddress bcc = (InternetAddress) renderedMessage.getRecipients(CC)[0];
                assert "test@example.com".equals(bcc.getAddress());
                assert "Pete Muir".equals(bcc.getPersonal());
                assert "bulk".equals(renderedMessage.getHeader("Precedence")[0]);
                // Importance
                assert renderedMessage.getHeader("X-Priority") != null;
                assert renderedMessage.getHeader("Priority") != null;
                assert renderedMessage.getHeader("Importance") != null;
                assert renderedMessage.getHeader("X-Priority").length == 1;
                assert renderedMessage.getHeader("Priority").length == 1;
                assert renderedMessage.getHeader("Importance").length == 1;
                assert "1".equals(renderedMessage.getHeader("X-Priority")[0]);
                assert "Urgent".equals(renderedMessage.getHeader("Priority")[0]);
                assert "high".equals(renderedMessage.getHeader("Importance")[0]);
                assert "Plain text email sent by Seam".equals(renderedMessage.getSubject());
                
                // Check the body
                
                assert renderedMessage.getContent() != null;
                MimeMultipart body = (MimeMultipart) renderedMessage.getContent();
                assert body.getCount() == 1;
                MimeBodyPart bodyPart = (MimeBodyPart) body.getBodyPart(0);
                assert bodyPart.getContent() != null;
                assert "inline".equals(bodyPart.getDisposition());
                assert bodyPart.isMimeType("text/plain");
            }
        }.run();
    }
    
    @Test
    public void testAttachmentErrors() throws Exception
    {
        
        new FacesRequest()
        {

            @Override
            protected void updateModelValues() throws Exception
            {
                
            }
            
            @Override
            protected void invokeApplication() throws Exception
            {   
                Person person = (Person) getValue("#{person}");
                
                person.setFirstname("Pete");
                person.setLastname("Muir");
                person.setAddress("test@example.com");
                
                // Test for an unavailable attachment
                
                Contexts.getEventContext().set("attachment", "/foo.pdf");
                
            }
        }.run();
    }
    
    @Test
    public void testAddressValidation() throws Exception
    {
        
        new FacesRequest()
        {

            @Override
            protected void updateModelValues() throws Exception
            {
                
            }
            
            @Override
            protected void invokeApplication() throws Exception
            {   
                Person person = (Person) getValue("#{person}");
                
                person.setFirstname("Pete");
                person.setLastname("Muir");                
                boolean exceptionThrown = false;
                          
                person.setAddress("testexample.com");
                
                try
                {
                    getRenderedMailMessage("/org/jboss/seam/example/mail/test/errors2.xhtml");
                }
                catch (FacesException e)
                {
                    assert e.getCause() instanceof AddressException;
                    AddressException ae = (AddressException) e.getCause();
                    assert ae.getMessage().startsWith("Missing final '@domain'");
                    exceptionThrown = true;
                }
                assert exceptionThrown;
             
            }
        }.run();
    }
    
    //JBSEAM-2109
    //@Test
    public void testReplyToErrors() throws Exception
    {
        
        new FacesRequest()
        {

            @Override
            protected void updateModelValues() throws Exception
            {
                
            }
            
            @Override
            protected void invokeApplication() throws Exception
            {   
                Person person = (Person) getValue("#{person}");
                
                person.setFirstname("Pete");
                person.setLastname("Muir");
                person.setAddress("test@example.com");
                
                boolean exceptionThrown = false;
                
                
                
                try
                {
                    getRenderedMailMessage("/org/jboss/seam/example/mail/test/errors3.xhtml");
                }
                catch (Exception e)
                {
                    assert e.getCause() instanceof AddressException;
                    AddressException ae = (AddressException) e.getCause();
                    System.out.println(ae.getMessage());
                    assert ae.getMessage().startsWith("Email cannot have more than one Reply-to address");
                    exceptionThrown = true;
                }
                assert exceptionThrown;
             
            }
        }.run();
    }
    
    @Test
    public void testFromErrors() throws Exception
    {
        
        new FacesRequest()
        {

            @Override
            protected void updateModelValues() throws Exception
            {
                
            }
            
            @Override
            protected void invokeApplication() throws Exception
            {   
                Person person = (Person) getValue("#{person}");
                
                person.setFirstname("Pete");
                person.setLastname("Muir");
                person.setAddress("test@example.com");
                
                boolean exceptionThrown = false;
                
                try
                {
                    getRenderedMailMessage("/org/jboss/seam/example/mail/test/errors4.xhtml");
                }
                catch (FacesException e)
                {
                    assert e.getCause() instanceof AddressException;
                    AddressException ae = (AddressException) e.getCause();
                    assert ae.getMessage().startsWith("Email cannot have more than one from address");
                    exceptionThrown = true;
                }
                assert exceptionThrown;
             
            }
        }.run();
    }
    
    @Test
    public void testSanitization() throws Exception
    {
        
        new FacesRequest()
        {

            @Override
            protected void updateModelValues() throws Exception
            {
                
            }
            
            @Override
            protected void invokeApplication() throws Exception
            { 
                Contexts.getEventContext().set("name", "Pete\nMuir");   
                MimeMessage renderedMessage = getRenderedMailMessage("/org/jboss/seam/example/mail/test/sanitization.xhtml");
                assert "Try out Seam!".equals(renderedMessage.getSubject());
                InternetAddress to = (InternetAddress) renderedMessage.getAllRecipients()[0];
                assert to.getAddress().equals("peter@email.tld");
                assert to.getPersonal().equals("Pete");
                assert renderedMessage.getFrom().length == 1;
                assert renderedMessage.getFrom()[0] instanceof InternetAddress;
                InternetAddress from = (InternetAddress) renderedMessage.getFrom()[0];
                assert from.getAddress().equals("peter@example.com");
                assert from.getPersonal().equals("Pete");
                assert renderedMessage.getHeader("Pete") != null;
                assert renderedMessage.getHeader("Pete").length == 1;
                assert "roll over".equals(renderedMessage.getHeader("Pete")[0]);
            }
        }.run();
    }
    
    @Test
    public void testTemplating() throws Exception
    {
        
        new FacesRequest()
        {

            @Override
            protected void updateModelValues() throws Exception
            {
                setValue("#{person.firstname}", "Pete");
                setValue("#{person.lastname}", "Muir");
                setValue("#{person.address}", "test@example.com");
            }
            
            @Override
            protected void invokeApplication() throws Exception
            {   
                MimeMessage renderedMessage = getRenderedMailMessage("/templating.xhtml");
             
                // Test the standard headers
                
                InternetAddress to = (InternetAddress) renderedMessage.getAllRecipients()[0];
                assert to.getAddress().equals("test@example.com");
                assert to.getPersonal().equals("Pete Muir");
                InternetAddress from = (InternetAddress) renderedMessage.getFrom()[0];
                assert from.getAddress().equals("do-not-reply@jboss.com");
                assert from.getPersonal().equals("Seam");
                assert "Templating with Seam Mail".equals(renderedMessage.getSubject());
                assert renderedMessage.getHeader("X-Priority") == null;
                assert renderedMessage.getHeader("Priority") == null;
                assert renderedMessage.getHeader("Importance") == null;
                
                // Check the body
                
                MimeMultipart body = (MimeMultipart) renderedMessage.getContent();
                
                // Check the alternative facet
                assert renderedMessage.getContentType().startsWith("multipart/mixed");
                assert body.getCount() == 1;
                MimeBodyPart bodyPart = (MimeBodyPart) body.getBodyPart(0);
                assert bodyPart.getContentType().startsWith("multipart/alternative");
                assert bodyPart.getContent() instanceof MimeMultipart;
                MimeMultipart bodyParts = (MimeMultipart) bodyPart.getContent();
                assert bodyParts.getCount() == 2;
                assert bodyParts.getBodyPart(0) instanceof MimeBodyPart;
                assert bodyParts.getBodyPart(1) instanceof MimeBodyPart;
                MimeBodyPart alternative = (MimeBodyPart) bodyParts.getBodyPart(0);
                MimeBodyPart html = (MimeBodyPart) bodyParts.getBodyPart(1);
                assert alternative.isMimeType("text/plain");
                assert "inline".equals(alternative.getDisposition());
                assert html.isMimeType("text/html");
                assert "inline".equals(html.getDisposition());       
            }
        }.run();
    }
}
