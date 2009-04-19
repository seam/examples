/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.exception;

/**
 * Thrown if an illegal GET request was made (missing parameters, etc).
 *
 * @author Christian Bauer
 */
public class InvalidWikiRequestException extends RuntimeException {

    public InvalidWikiRequestException(String s) {
        super(s);
    }
}
