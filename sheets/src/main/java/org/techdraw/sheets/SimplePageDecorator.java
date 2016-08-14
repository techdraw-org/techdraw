package org.techdraw.sheets;

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
    public double headerHeight() {
        if(headerText == null)
            return 0;

        return 5; // TODO: to calculate height
    }

    @Override
    public double footerHeight() {
        if(footerText == null)
            return 0;

        return 5; // TODO: to calculate height
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
