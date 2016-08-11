package org.techdraw.sheets.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Miroslav Kravec
 */
public interface BoxedElement {
    Element toSvgElement(Document doc, String svgNS);

    double getX();
    double getY();
    double getWidth();
    double getHeight();

    void setX(double x);
    void setY(double y);
    void setWidth(double width);
    void setHeight(double height);
}
