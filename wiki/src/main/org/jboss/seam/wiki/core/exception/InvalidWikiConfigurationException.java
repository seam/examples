/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.exception;

/**
 * @author Christian Bauer
 */
public class InvalidWikiConfigurationException extends RuntimeException {

    public InvalidWikiConfigurationException() {
        super();
    }

    public InvalidWikiConfigurationException(String s) {
        super(s);
    }

    public InvalidWikiConfigurationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public InvalidWikiConfigurationException(Throwable throwable) {
        super(throwable);
    }
}
