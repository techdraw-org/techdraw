package org.kravemir.techdraw.containers;

import org.kravemir.techdraw.api.BoxedElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Miroslav Kravec
 */
public abstract class AbstractBoxedElement implements BoxedElement {

    private boolean calculationNeeded = true;

    private String tagName;

    protected double x = 0;
    protected double y = 0;
    protected double width = 0;
    protected double height = 0;

    public AbstractBoxedElement(String tagName) {
        this.tagName = tagName;
    }

    protected abstract void recalculateSize();
    protected void requestRecalculation() {
        calculationNeeded = true;
    }

    protected void recalculateSizeIfNeeded() {
        if(calculationNeeded) {
            recalculateSize();
            calculationNeeded = false;
        }
    }

    protected abstract void populateElementAttributes(Element e, Document doc, String svgNS);

    @Override
    public Element toSvgElement(Document doc, String svgNS) {
        Element element = doc.createElementNS(svgNS,tagName);
        populateElementAttributes(element, doc, svgNS);
        return element;
    }

    @Override
    public double getX() {
        recalculateSizeIfNeeded();
        return x;
    }

    @Override
    public double getY() {
        recalculateSizeIfNeeded();
        return y;
    }

    @Override
    public double getWidth() {
        recalculateSizeIfNeeded();
        return width;
    }

    @Override
    public double getHeight() {
        recalculateSizeIfNeeded();
        return height;
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public void setWidth(double width) {
        this.width = width;
    }

    @Override
    public void setHeight(double height) {
        this.height = height;
    }
}
