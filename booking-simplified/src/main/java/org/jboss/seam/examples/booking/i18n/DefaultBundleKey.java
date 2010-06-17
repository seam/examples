package org.jboss.seam.examples.booking.i18n;

import org.jboss.seam.international.status.builder.BundleKey;

/**
 * @author Dan Allen
 */
public class DefaultBundleKey extends BundleKey {

   public static final String DEFAULT_BUNDLE_NAME = "messages";

   public DefaultBundleKey(String key)
   {
      super(DEFAULT_BUNDLE_NAME, key);
   }
}
