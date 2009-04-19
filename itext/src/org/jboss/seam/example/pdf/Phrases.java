package org.jboss.seam.example.pdf;


import org.jboss.seam.annotations.Name;

@Name("phrases")
public class Phrases {

    public String getChinese() {
        return "\u5341\u950a\u57cb\u4f0f";
    }
    
    public String getJapanese() {
        return "\u8ab0\u3082\u77e5\u3089\u306a\u3044";
    }
    
    public String getKorean() {
        return "\ube48\uc9d1";
    }
    
}
