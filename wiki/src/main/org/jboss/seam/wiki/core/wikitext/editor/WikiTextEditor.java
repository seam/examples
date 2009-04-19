/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.wikitext.editor;

import antlr.*;
import org.jboss.seam.Component;
import org.jboss.seam.international.Messages;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.text.SeamTextLexer;
import org.jboss.seam.text.SeamTextParser;
import org.jboss.seam.text.SeamTextParserTokenTypes;
import org.jboss.seam.wiki.core.action.Validatable;
import org.jboss.seam.wiki.core.model.WikiFile;
import org.jboss.seam.wiki.core.wikitext.engine.WikiLinkResolver;

import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

/**
 * A wiki (or plain) text editor.
 *
 * @author Christian Bauer
 */
public class WikiTextEditor implements Validatable, Serializable {

    Log log = Logging.getLog(WikiTextEditor.class);

    // Construction time
    private String key;
    private int valueMaxLength = 32767;
    private boolean valueRequired = true;
    private boolean allowPlaintext = false;
    private int rows = 20;

    // Editing
    private String value;
    private boolean valid = true;
    private boolean valuePlaintext;
    private boolean previewEnabled;
    private Set<WikiFile> linkTargets;
    private WikiTextEditorError lastValidationError;

    public WikiTextEditor(String key) {
        this.key = key;
    }

    public WikiTextEditor(String key, int valueMaxLength) {
        this.key = key;
        this.valueMaxLength = valueMaxLength;
    }

    public WikiTextEditor(String key, int valueMaxLength, boolean valueRequired) {
        this.key = key;
        this.valueMaxLength = valueMaxLength;
        this.valueRequired = valueRequired;
    }

    public WikiTextEditor(String key, int valueMaxLength, boolean valueRequired, boolean allowPlaintext) {
        this.key = key;
        this.valueMaxLength = valueMaxLength;
        this.valueRequired = valueRequired;
        this.allowPlaintext = allowPlaintext;
    }

    public WikiTextEditor(String key, int valueMaxLength, boolean valueRequired, boolean allowPlaintext, int rows) {
        this.key = key;
        this.valueMaxLength = valueMaxLength;
        this.valueRequired = valueRequired;
        this.allowPlaintext = allowPlaintext;
        this.rows = rows;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        // Stupid Internet Explorer textarea puts carriage returns inside the text, we don't want any of that
        this.value = value != null ? value.replaceAll("\r", "") : value;
    }

    public int getValueMaxLength() {
        return valueMaxLength;
    }

    public void setValueMaxLength(int valueMaxLength) {
        this.valueMaxLength = valueMaxLength;
    }

    public boolean isValueRequired() {
        return valueRequired;
    }

    public void setValueRequired(boolean valueRequired) {
        this.valueRequired = valueRequired;
    }

    public boolean isAllowPlaintext() {
        return allowPlaintext;
    }

    public void setAllowPlaintext(boolean allowPlaintext) {
        this.allowPlaintext = allowPlaintext;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValuePlaintext() {
        return valuePlaintext;
    }

    public void setValuePlaintext(boolean valuePlaintext) {
        this.valuePlaintext = valuePlaintext;
    }

    public boolean isPreviewEnabled() {
        return previewEnabled;
    }

    public void setPreviewEnabled(boolean previewEnabled) {
        if (previewEnabled) {
            validate();
        } else {
            setValid(true);
        }
        this.previewEnabled = previewEnabled;
    }

    public WikiTextEditorError getLastValidationError() {
        return lastValidationError;
    }

    public void setLastValidationError(WikiTextEditorError lastValidationError) {
        this.lastValidationError = lastValidationError;
    }

    public Set<WikiFile> getLinkTargets() {
        return linkTargets;
    }

    public void setValueAndConvertLinks(Long areaNumber, String value) {
        log.debug("setting value and resolving wiki://links to clear text");
        WikiLinkResolver wikiLinkResolver = (WikiLinkResolver) Component.getInstance("wikiLinkResolver");
        setValue(wikiLinkResolver.convertFromWikiProtocol(areaNumber, value));
    }

    public String getValueAndConvertLinks(Long areaNumber) {
        log.debug("setting value and resolving clear text to wiki://links");
        WikiLinkResolver wikiLinkResolver = (WikiLinkResolver)Component.getInstance("wikiLinkResolver");
        linkTargets = new HashSet<WikiFile>();
        return wikiLinkResolver.convertToWikiProtocol(linkTargets, areaNumber, getValue());
    }

    public int getRemainingCharacters() {
        return getValue() != null ? getValueMaxLength() - getValue().length() : getValueMaxLength();
    }

    public void switchPlaintext() {
        // If the user wants to switch from plain text back to wiki text, do validation
        if (!isValuePlaintext()) {
            validate();
            // Allow only if valid wiki text markup
            setValuePlaintext(!isValid());
        } else {
            // If the user wants plain text, then we can discard any validation errors
            setValid(true);
            lastValidationError = null;
        }
    }

    public void validate() {
        log.debug("validating value of text editor: " + key);
        setValid(false);
        if (valueRequired && (value == null || value.length() == 0)) {
            log.debug("validation failed for required but null or empty wiki text with key: " + key);
            lastValidationError = new WikiTextEditorError(
                Messages.instance().get("lacewiki.msg.wikiTextValidator.EmptyWikiText")
            );
            return;
        }
        if (value != null && value.length() > getValueMaxLength()) {
            log.debug("validation failed for too long wiki text with key: " + key);
            lastValidationError = new WikiTextEditorError(
                Messages.instance().get("lacewiki.msg.wikiTextValidator.MaxLengthExceeded")
            );
            return;
        }

        lastValidationError = null;
        setValid(true);
        if (!isValuePlaintext()) {
            try {
                SeamTextParser parser = getValidationParser(value);
                parser.startRule();
                setValid(true);
            }
            // Error handling for ANTLR lexer/parser errors, see
            // http://www.doc.ic.ac.uk/lab/secondyear/Antlr/err.html
            catch (TokenStreamException tse) {
                setValid(false);
                // Problem with the token input stream
                throw new RuntimeException(tse);
            } catch (RecognitionException re) {
                setValid(false);
                lastValidationError = convertException(re);
            }
        }
        log.debug("completed validation of text editor value for key: " + key);
    }

    protected SeamTextParser getValidationParser(String text) {
        Reader r = new StringReader(text);
        SeamTextLexer lexer = new SeamTextLexer(r);
        SeamTextParser parser = new SeamTextParser(lexer);
        parser.setSanitizer(
            new SeamTextParser.DefaultSanitizer() {
                @Override
                public void validateLinkTagURI(Token token, String s) throws SemanticException {
                    // Disable this part of the validation
                }
                @Override
                public String getInvalidURIMessage(String uri) {
                    return Messages.instance().get("lacewiki.msg.wikiTextValidator.InvalidURI");
                }
                @Override
                public String getInvalidElementMessage(String elementName) {
                    return Messages.instance().get("lacewiki.msg.wikiTextValidator.InvalidElement");
                }
                @Override
                public String getInvalidAttributeMessage(String elementName, String attributeName) {
                    return Messages.instance().get("lacewiki.msg.wikiTextValidator.InvalidAttribute")
                            + " '" + attributeName + "'";
                }
                @Override
                public String getInvalidAttributeValueMessage(String elementName, String attributeName, String value) {
                    return Messages.instance().get("lacewiki.msg.wikiTextValidator.InvalidAttributeValue")
                            + " '" + attributeName + "'";
                }
            }
        );
        return parser;
    }

    // This tries to make sense of the totally useless exceptions thrown by ANTLR parser.
    protected WikiTextEditorError convertException(RecognitionException ex) {
        WikiTextEditorError error = new WikiTextEditorError();
        if (ex instanceof MismatchedTokenException) {

            MismatchedTokenException tokenException = (MismatchedTokenException) ex;
            String expecting = SeamTextParser._tokenNames[tokenException.expecting];

            String found = "";
            if (tokenException.token.getType() != SeamTextParserTokenTypes.EOF) {
                error.setPosition(tokenException.getColumn());
                found = ", " + Messages.instance().get("lacewiki.msg.wikiTextValidator.InsteadFound")
                        + " " + SeamTextParser._tokenNames[tokenException.token.getType()];
            }

            error.setFormattingErrorMessage(
                Messages.instance().get("lacewiki.msg.wikiTextValidator.ReachedEndAndMissing")
                + " " + expecting + found
            );

        } else if (ex instanceof SeamTextParser.HtmlRecognitionException) {

            SeamTextParser.HtmlRecognitionException htmlException = (SeamTextParser.HtmlRecognitionException) ex;
            Token openingElement = htmlException.getOpeningElement();
            String elementName = openingElement.getText();
            String detailMsg;
            if (!(htmlException.getCause() instanceof MismatchedTokenException)) {
                detailMsg = ", " + convertException((RecognitionException)htmlException.getCause()).getMessage();
            } else {
                detailMsg = "";
            }
            error.setFormattingErrorMessage(
                Messages.instance().get("lacewiki.msg.wikiTextValidator.UnclosedInvalidHTML")
                + " '<"+elementName+">'" + detailMsg
            );
            error.setPosition(openingElement.getColumn());

        } else if (ex instanceof NoViableAltException) {

            NoViableAltException altException = (NoViableAltException) ex;
            String unexpected = SeamTextParser._tokenNames[altException.token.getType()];

            error.setFormattingErrorMessage(
                Messages.instance().get("lacewiki.msg.wikiTextValidator.WrongPositionFor")
                + " " + unexpected
            );
            error.setPosition(altException.getColumn());

        } else {
            error.setFormattingErrorMessage(ex.getMessage());
            error.setPosition(ex.getColumn());
        }
        return error;
    }

}
