package org.kravemir.techdraw.containers;

import org.kravemir.techdraw.api.BoxedElement;
import org.w3c.dom.Element;

/**
 * @author Miroslav Kravec
 */
public class CustomBoxedElement implements BoxedElement {

    private Element element;
    private double x;
    private double y;
    private double width;
    private double height;

    public CustomBoxedElement(Element element, double width, double height) {
        this.element = element;
        this.width = width;
        this.height = height;
    }

    @Override
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    @Override
    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    @Override
    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
