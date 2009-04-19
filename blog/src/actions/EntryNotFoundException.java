package actions;

import javax.ejb.ApplicationException;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.exception.HttpError;

@ApplicationException(rollback=true)
@HttpError(errorCode=HttpServletResponse.SC_NOT_FOUND)
public class EntryNotFoundException extends Exception
{
   EntryNotFoundException(String id)
   {
      super("entry not found: " + id);
   }
}
