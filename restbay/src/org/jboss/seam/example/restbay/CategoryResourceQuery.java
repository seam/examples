package org.jboss.seam.example.restbay;

import javax.ws.rs.Path;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.resteasy.ResourceQuery;

/**
 * Example of ResourceQuery usage. Used for testing purposes.
 * 
 * @author Jozef Hartinger
 * 
 */
@Name("categoryResourceQuery")
@Path("extendedCategory")
public class CategoryResourceQuery extends ResourceQuery<Category>
{

}
