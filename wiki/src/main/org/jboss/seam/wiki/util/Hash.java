package org.jboss.seam.wiki.util;

import java.security.MessageDigest;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.AutoCreate;

/**
 * Not super safe, should use a random salt, prepended later on the digest.
 * Should also iterate the hashing a few thousand times to make brute force
 * attacks more difficult. Oh well, probably good enough for storing things
 * in an internal database.
 * <p/>
 */
@Name("hashUtil")
@AutoCreate
public class Hash {
    String hashFunction = "MD5";
    String charset = "UTF-8";

    private static final char[] DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    public String hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashFunction);
            md.update(text.getBytes(charset));
            byte[] raw = md.digest();
            return new String(encodeHex(raw));
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

    public static char[] encodeHex(byte[] data) {

        int l = data.length;

        char[] out = new char[l << 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }

        return out;
    }

}
