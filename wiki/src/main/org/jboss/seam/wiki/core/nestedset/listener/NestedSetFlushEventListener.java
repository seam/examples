/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.ejb.event.EJB3FlushEventListener;
import org.hibernate.event.EventSource;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * TODO: This is really the issue why a nice nested set implementation with Hibernate and MySQL is impossible!
 * </p>
 * <p>
 * Any nested set tree modification potentially updates all rows in a database table. This
 * requires several <tt>UPDATE</tt> statements, and also <tt>INSERT</tt> and <tt>DELETE</tt>.
 * Any concurrent insertion or deletion to the rows betwen <tt>UPDATE</tt> statements would be fatal and
 * corrupt the tree information. Transactions that modify nested set data should be serialized.
 * </p>
 * <p>
 * For example, without any additional locking we'd run into the following deadlock situation with MySQL. Consider
 * the following threads and the required modifications when two nested set node are deleted concurrently:
 * </p>
 *
 * <pre>
 *    Thread I         Nested Set Nodes       Thread II
 * --- 1. DELETE --->         A
 *
 *                            B           <--- 2. DELETE ---
 *
 * --- 2. UPDATE --->         B
 *  (Waits for lock)
 *                            A           <--- 3. UPDATE ---
 *                                          (Waits for lock)
 *
 * </pre>
 *
 * <p>
 * This results in a MySQL deadlock detection exception and a rollback of the transaction in Thread II.
 * The usual solution is to lock the whole table(s) to force serialized execution of threads that modify
 * nested set tree state.
 * </p>
 * <p>
 * However, because MySQL has an unusable locking system (locking a table commits the current transaction, you
 * need to lock all tables you are going to use from that point on, etc.), and because portability is
 * a concern of this nested set implementation, we work around the problem with an in-memory exclusive lock.
 * </p>
 * <p>
 * The situation is further complicated by Hibernate's flushing/eventing behavior. There is no way how we can
 * only lock for nested set updates, we need to lock on every execution of a flush. WARNING: This severely
 * degrades performance of your application, as any automatic or manual flush of the Hibernate persistence context
 * will be serialized application-wide! Luckily, we can only lock when deletions or insertions are queued so
 * flushing with no modifications in the persistence context (e.g. before a query) is not affected.
 * </p>
 * <p>
 * <b>NOTE:</b> This does NOT work if several applications modify the nested set tree on the same database tables!
 * </p>
 *
 * @author Christian Bauer
 */
public class NestedSetFlushEventListener extends EJB3FlushEventListener {

    private static final Log log = LogFactory.getLog(NestedSetFlushEventListener.class);

    private static final int LOCK_TIMEOUT_SECONDS = 15;

    private static final Lock lock = new ReentrantLock(true);

    @Override
    protected void performExecutions(EventSource eventSource) throws HibernateException {

        if (eventSource.getActionQueue().areInsertionsOrDeletionsQueued()) {
            try {
                log.debug("trying to obtain exclusive lock for " + LOCK_TIMEOUT_SECONDS +
                          " seconds before performing database modifications during flush");
                if (lock.tryLock(LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    log.debug("successfully obtained lock, executing flush");
                    try {
                        super.performExecutions(eventSource);
                    } finally {
                        log.debug("releasing exclusive lock after flush execution");
                        lock.unlock();
                    }
                } else {
                    throw new NestedSetLockTimeoutException("Could not aquire exclusive lock during database flush");
                }
            } catch (InterruptedException ex) {
                throw new NestedSetLockTimeoutException("Current thread could not aquire lock, has been interrupted");
            }
        } else {
            super.performExecutions(eventSource);
        }

    }

}