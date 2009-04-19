/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.wikitext.editor;

import org.jboss.seam.international.Messages;

/**
 * Encapsulates an error message and, if possible, a location of the error in
 * the character stream.
 *
 * @author Christian Bauer
*/
public class WikiTextEditorError {

    private String message;
    private int position = -1;

    public WikiTextEditorError() {}

    public WikiTextEditorError(String message) {
        setMessage(message);
    }

    public WikiTextEditorError(String message, int position) {
        this(message);
        this.position = position;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setFormattingErrorMessage(String message) {
        this.message =
                Messages.instance().get("lacewiki.msg.wikiTextValidator.FormattingErrorPrefix")
                + " " + message + ".";
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
