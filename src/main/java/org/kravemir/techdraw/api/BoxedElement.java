package org.kravemir.techdraw.api;

import org.w3c.dom.Element;

/**
 * @author Miroslav Kravec
 */
public interface BoxedElement {
    Element getElement();

    double getX();
    double getY();
    double getWidth();
    double getHeight();

    void setX(double x);
    void setY(double y);
    void setWidth(double width);
    void setHeight(double height);
}
