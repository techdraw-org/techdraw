package org.techdraw.sheets.elements;

import org.techdraw.sheets.containers.AbstractBoxedElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Miroslav Kravec
 */
public class LineElement extends AbstractBoxedElement {

    private final double x2;
    private final double y2;

    public LineElement(double x, double y, double x2, double y2) {
        this(x,y,x2,y2,"stroke: black; stroke-width: 0.2;");
    }

    public LineElement(double x, double y, double x2, double y2, String style) {
        super("line",style);
        setX(x);
        setY(y);
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    protected void recalculateSize() {

    }

    @Override
    protected void populateElementAttributes(Element e, Document doc, String svgNS) {
        e.setAttribute("x1", String.valueOf(x));
        e.setAttribute("y1", String.valueOf(y));
        e.setAttribute("x2", String.valueOf(x2));
        e.setAttribute("y2", String.valueOf(y2));
    }
}
