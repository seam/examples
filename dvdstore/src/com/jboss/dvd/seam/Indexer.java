//$Id$
package com.jboss.dvd.seam;

import java.util.Date;

/**
 * @author Emmanuel Bernard
 */
public interface Indexer
{
   Date getLastIndexingTime();
   void index();
   void stop();
}
