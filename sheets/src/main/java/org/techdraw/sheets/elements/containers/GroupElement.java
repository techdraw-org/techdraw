package org.techdraw.sheets.elements.containers;

import org.techdraw.sheets.elements.spi.BoxedElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGGElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Miroslav Kravec
 */
public class GroupElement extends AbstractBoxedElement {
    private List<BoxedElement> children = new ArrayList<>();
    private double[] transformMatrix = null;

    public GroupElement() {
        super("g");
    }

    @Override
    protected void recalculateSize() {

    }

    @Override
    protected void populateElementAttributes(Element e, Document doc, String svgNS) {
        SVGGElement group = (SVGGElement) e;
        if(transformMatrix != null) {
            double[] tm = transformMatrix;
            group.setAttribute("transform", String.format("matrix(%f,%f,%f,%f,%f,%f)", tm[0], tm[1], tm[2], tm[3], tm[4], tm[5]));
        }

        for(BoxedElement child : children) {
            group.appendChild(child.toSvgElement(doc,svgNS));
        }
    }

    public void addChild(BoxedElement element) {
        children.add(element);
    }

    public void addChild(BoxedElement[] elements) {
        children.addAll(Arrays.asList(elements));
    }

    public void clearChildren() {
        children.clear();
    }

    public void setMatrixTransform(double... values) {
        if(values.length != 6)
            throw new IllegalStateException();
        this.transformMatrix = values;
    }

    public static GroupElement translate(BoxedElement child, double x, double y) {
        GroupElement g = new GroupElement();
        g.setMatrixTransform(1,0,0,1,x,y);
        g.addChild(child);
        return g;
    }
}
