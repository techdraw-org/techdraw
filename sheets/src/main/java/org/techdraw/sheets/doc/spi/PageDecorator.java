package org.techdraw.sheets.doc.spi;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Miroslav Kravec
 */
public interface PageDecorator {
    double headerHeight(double marginTop);
    double footerHeight(double marginBottom);

    void applyPageDecoration(Element svgRoot, Document doc, String svgNS);
}
