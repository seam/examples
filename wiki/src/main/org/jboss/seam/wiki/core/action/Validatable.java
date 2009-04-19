/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

/**
 * Marks a component that has to be valid before a CUD operation can continue.
 * <p>
 * These components are validated during save or update operations, and they typically queue
 * a status message for display on the UI. The validation method is decoupled from the validation
 * result, this simplifies user interface design.
 * </p>
 *
 * @author Christian Bauer
 */
public interface Validatable {

    public void validate();
    public boolean isValid();
}
