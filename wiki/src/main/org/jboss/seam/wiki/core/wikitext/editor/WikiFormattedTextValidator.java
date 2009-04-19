/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.wikitext.editor;

import org.jboss.seam.ui.validator.FormattedTextValidator;
import org.jboss.seam.text.SeamTextParser;
import antlr.SemanticException;
import antlr.Token;

/**
 * Disables the Seam Text validation for link tags, so wiki links are OK.
 * <p>
 * Also provides some conversion to i18n error messages.
 * </p>
 *
 * TODO: Finish the i18n and well, maybe we should just duplicate the
 * Seam validator here to drop that dependency on the UI package...
 *
 * @author Christian Bauer
 */
public class WikiFormattedTextValidator extends FormattedTextValidator {

    public SeamTextParser getSeamTextParser(String s) {
        SeamTextParser parser = super.getSeamTextParser(s);
        parser.setSanitizer(
            new SeamTextParser.DefaultSanitizer() {

                // Disable this method
                public void validateLinkTagURI(Token token, String s) throws SemanticException {}

                @Override
                public String getInvalidURIMessage(String s) {
                    return super.getInvalidURIMessage(s);
                }

                @Override
                public String getInvalidElementMessage(String s) {
                    return super.getInvalidElementMessage(s);
                }

                @Override
                public String getInvalidAttributeMessage(String s, String s1) {
                    return super.getInvalidAttributeMessage(s, s1);
                }

                @Override
                public String getInvalidAttributeValueMessage(String s, String s1, String s2) {
                    return super.getInvalidAttributeValueMessage(s, s1, s2);
                }
            }
        );
        return parser;
    }

    @Override
    public String getNoViableAltErrorMessage(String s, String s1) {
        return super.getNoViableAltErrorMessage(s, s1);
    }

    @Override
    public String getMismatchedTokenErrorMessage(String s, String s1) {
        return super.getMismatchedTokenErrorMessage(s, s1);
    }

    @Override
    public String getSemanticErrorMessage(String s) {
        return super.getSemanticErrorMessage(s);
    }

    @Override
    public int getNumberOfCharsBeforeErrorLocation() {
        return 20;
    }

    @Override
    public int getNumberOfCharsAfterErrorLocation() {
        return 20;
    }

}
