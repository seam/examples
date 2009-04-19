package org.jboss.seam.example.seamspace;

import java.security.MessageDigest;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.util.Hex;

@Name("hash")
public class Hash {
    String hashFunction = "MD5";
    String charset      = "UTF-8";
    
    public String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashFunction);
            md.update(password.getBytes(charset));
            byte[] raw = md.digest();
            return new String(Hex.encodeHex(raw));
        } 
        catch (Exception e) {
            throw new RuntimeException(e);        
        }
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getHashFunction() {
        return hashFunction;
    }

    public void setHashFunction(String hashFunction) {
        this.hashFunction = hashFunction;
    }    
    
    public static Hash instance() {
        return (Hash) Component.getInstance(Hash.class);
    }
}
