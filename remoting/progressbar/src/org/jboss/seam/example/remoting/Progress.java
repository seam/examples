package org.jboss.seam.example.remoting;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Contains progress information.
 *
 * @author Shane Bryzak
 * @version 1.0
 */
@Name("progress")
@Scope(ScopeType.SESSION)
public class Progress {

  private int percentComplete;
  private String status;

  public int getPercentComplete()
  {
    return percentComplete;
  }

  public void setPercentComplete(int percentComplete)
  {
    this.percentComplete = percentComplete;
  }

  public String getStatus()
  {
    return status;
  }

  public void setStatus(String status)
  {
    this.status = status;
  }
}
