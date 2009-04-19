package org.jboss.seam.example.pdf;

import java.awt.*;
import javax.swing.*;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;

@Name("swing")
public class SwingComponent
{
    @Create
    public void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch(Exception e) {
            System.out.println("Error setting Java LAF: " + e);
        }
    }
    
    public Component getLabel1() {
        JLabel label = new JLabel("Hello, Seam", SwingConstants.CENTER);

        return label;        
    }

    public Component getLabel2() {
        JLabel label = new JLabel("A label!", SwingConstants.CENTER);

        return label;        
    }

}
