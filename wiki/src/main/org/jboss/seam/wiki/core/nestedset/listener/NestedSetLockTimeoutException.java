package org.jboss.seam.wiki.core.nestedset.listener;

/**
 * @author Christian Bauer
 */
public class NestedSetLockTimeoutException extends RuntimeException {

    public NestedSetLockTimeoutException() {
        super();
    }

    public NestedSetLockTimeoutException(String s) {
        super(s);
    }

    public NestedSetLockTimeoutException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NestedSetLockTimeoutException(Throwable throwable) {
        super(throwable);
    }
}
