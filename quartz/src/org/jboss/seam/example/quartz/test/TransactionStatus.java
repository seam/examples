package org.jboss.seam.example.quartz.test;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

/**
 * @author Pete Muir
 *
 */
@Name("transactionStatus")
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class TransactionStatus
{
    private Object id;
    private boolean transactionSucceded;
    private boolean transactionCompleted;
    /**
     * @return the transactionSuccess
     */
    public boolean getTransactionSucceded()
    {
        return this.transactionSucceded;
    }
    /**
     * @param transactionSuccess the transactionSuccess to set
     */
    public void setTransactionSucceded(boolean transactionSuccess)
    {
        this.transactionSucceded = transactionSuccess;
    }
    /**
     * @return the transactionCompleted
     */
    public boolean getTransactionCompleted()
    {
        return this.transactionCompleted;
    }
    /**
     * @param transactionCompleted the transactionCompleted to set
     */
    public void setTransactionCompleted(boolean transactionCompleted)
    {
        this.transactionCompleted = transactionCompleted;
    }
    
    public static void clear()
    {
        Contexts.getApplicationContext().remove("transactionStatus");
    }
    
    public static TransactionStatus instance()
    {
        return (TransactionStatus) Component.getInstance("transactionStatus");
    }
    /**
     * @return the id
     */
    public Object getId()
    {
        return this.id;
    }
    /**
     * @param id the id to set
     */
    public void setId(Object id)
    {
        this.id = id;
    }

}
