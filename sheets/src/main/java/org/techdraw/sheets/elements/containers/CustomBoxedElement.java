package org.techdraw.sheets.elements.containers;

import org.techdraw.sheets.elements.spi.BoxedElement;

/**
 * @author Miroslav Kravec
 */
public abstract class CustomBoxedElement implements BoxedElement {

    private double x;
    private double y;
    private double width;
    private double height;

    public CustomBoxedElement(double width, double height) {
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
