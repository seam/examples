package org.jboss.seam.wiki.core.wikitext.renderer;

import java.util.Set;
import java.util.LinkedHashSet;

import antlr.RecognitionException;
import antlr.ANTLRException;
import org.jboss.seam.wiki.core.wikitext.engine.WikiTextParser;
import org.jboss.seam.wiki.core.model.WikiTextMacro;

public class MacroWikiTextRenderer extends NullWikiTextRenderer {

    private Set<WikiTextMacro> macros = new LinkedHashSet<WikiTextMacro>();

    @Override
    public String renderMacro(WikiTextMacro macro) {
        macros.add(macro);
        return null;
    }

    public Set<WikiTextMacro> getMacros() {
        return macros;
    }

    public static MacroWikiTextRenderer renderMacros(String wikiText) {
        WikiTextParser parser = new WikiTextParser(wikiText, false, false);
        MacroWikiTextRenderer renderer = new MacroWikiTextRenderer();
        try {
            parser.setRenderer(renderer).parse();
        } catch (RecognitionException rex) {
            // Swallowing, we don't really care if there was a parse error
        } catch (ANTLRException ex) {
            // All other errors are fatal;
            throw new RuntimeException(ex);
        }
        return renderer;
    }

}
