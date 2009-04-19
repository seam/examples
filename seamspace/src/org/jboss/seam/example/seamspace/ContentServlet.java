package org.jboss.seam.example.seamspace;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.jboss.seam.Component;

/**
 * Serves images and other member content
 * 
 * @author Shane Bryzak
 */
public class ContentServlet extends HttpServlet
{
   private static final long serialVersionUID = -8461940507242022217L;

   private static final String IMAGES_PATH = "/images";

   /**
    * The maximum width allowed for image rescaling
    */
   private static final int MAX_IMAGE_WIDTH = 1024;
   
   private byte[] noImage;
   
   public ContentServlet()
   {
      InputStream in = getClass().getResourceAsStream("/images/no_image.png");
      if (in != null)
      {
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         byte[] buffer = new byte[512];
         try
         {
            int read = in.read(buffer);
            while (read != -1)
            {
               out.write(buffer, 0, read);
               read = in.read(buffer);
            }
            
            noImage = out.toByteArray();
         } 
         catch (IOException e) { }
      }
      
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException
   {
      if (IMAGES_PATH.equals(request.getPathInfo()))
      {
         ContentAction contentAction = (ContentAction) Component.getInstance(ContentAction.class);

         String id = request.getParameter("id");
         MemberImage mi = (id != null && !"".equals(id)) ? 
               contentAction.getImage(Integer.parseInt(id)) : null;
         
         String contentType = null;
         byte[] data = null;
         
         if (mi != null && mi.getData() != null && mi.getData().length > 0)
         {
            contentType = mi.getContentType();
            data = mi.getData();
         }
         else if (noImage != null)
         {
            contentType = "image/png";
            data = noImage;
         }
         
         if (data != null)
         {
            response.setContentType(contentType);
   
            boolean rescale = false;
            int width = 0;
            ImageIcon icon = null;
   
            // Check if the image needs to be rescaled
            if (request.getParameter("width") != null)
            {
               width = Math.min(MAX_IMAGE_WIDTH, Integer.parseInt(request
                     .getParameter("width")));
               icon = new ImageIcon(data);
               if (width > 0 && width != icon.getIconWidth())
                  rescale = true;
            }
   
            // Rescale the image if required
            if (rescale)
            {
               double ratio = (double) width / icon.getIconWidth();
               int height = (int) (icon.getIconHeight() * ratio);
               
               int imageType = "image/png".equals(contentType) ? 
                     BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;                  
               BufferedImage bImg = new BufferedImage(width, height, imageType);
               Graphics2D g2d = bImg.createGraphics();
               g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                     RenderingHints.VALUE_INTERPOLATION_BICUBIC);
               g2d.drawImage(icon.getImage(), 0, 0, width, height, null);
               g2d.dispose();
   
               String formatName = "";
               if (contentType != null && contentType.indexOf("png") != -1)
                  formatName = "png";
               else if (contentType != null && (contentType.indexOf("jpg") != -1) ||
                     contentType.indexOf("jpeg") != -1)
                  formatName = "jpeg";
   
               ImageIO.write(bImg, formatName, response.getOutputStream());
            }
            else
            {
               response.getOutputStream().write(data);
            }
         }

         response.getOutputStream().flush();
      }
   }
}
