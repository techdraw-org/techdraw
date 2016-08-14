package org.techdraw.sheets;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Miroslav Kravec
 */
public interface PageDecorator {
    double headerHeight();
    double footerHeight();

    void applyPageDecoration(Element svgRoot, Document doc, String svgNS);
}
