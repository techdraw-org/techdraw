package org.techdraw.sheets.doc.decorators;

import org.techdraw.sheets.doc.spi.PageDecorator;
import org.techdraw.sheets.elements.TextElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Miroslav Kravec
 */
public class SimplePageDecorator implements PageDecorator {

    private String headerText;
    private String footerText;

    @Override
    public double headerHeight(double marginTop) {
        if(headerText == null)
            return 0;

        return Math.max(0, 7 - marginTop); // TODO: to calculate height
    }

    @Override
    public double footerHeight(double marginBottom) {
        if(footerText == null)
            return 0;

        return Math.max(0, 7 - marginBottom); // TODO: to calculate height
    }

    @Override
    public void applyPageDecoration(Element svgRoot, Document doc, String svgNS) {
        if(headerText != null) {
            TextElement header = new TextElement(headerText, "ISOCPEUR", 5);
            header.setX(10);
            header.setY(2.5);
            svgRoot.appendChild(header.toSvgElement(doc, svgNS));
        }

        if(footerText != null) {
            TextElement footer = new TextElement(footerText, "ISOCPEUR", 5);
            footer.setX(10);
            footer.setY(290 - 2.5);
            svgRoot.appendChild(footer.toSvgElement(doc, svgNS));
        }
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

    public String getFooterText() {
        return footerText;
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
    }
}
