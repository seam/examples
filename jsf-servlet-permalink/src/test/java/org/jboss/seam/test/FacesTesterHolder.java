package org.jboss.seam.test;

import com.steeplesoft.jsf.facestester.FacesTester;

/**
 * @author Dan Allen
 */
public class FacesTesterHolder {
    private static FacesTester tester;

    public static synchronized FacesTester instance() {
        if (tester == null) {
            tester = new FacesTester();
        }

        return tester;
    }
}
