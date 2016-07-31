package org.kravemir.techdraw.deskdraw;

import org.kravemir.techdraw.api.BoxedElement;
import org.kravemir.techdraw.containers.CustomBoxedElement;
import org.kravemir.techdraw.elements.TextElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGGElement;
import org.w3c.dom.svg.SVGLineElement;

import static java.lang.Math.sqrt;

/**
 * @author Miroslav Kravec
 */
public class Desk {

    double a;
    double b;
    double width;
    boolean[] edges;

    public Desk(double a, double b, double width, boolean[] edges) {
        this.a = a;
        this.b = b;
        this.width = width;
        this.edges = edges;
    }

    Element makeArrowDelim(Document doc, String svgNS, double x, double y, double mw, double mas, boolean outerArrows) {
        double orientation = outerArrows ? -1 : 1;
        Element path = doc.createElementNS(svgNS,"path");
        path.setAttribute(
                "d",
                //"M0,0L0,10M0,5L2.5,2.5M0,5L2.5,7.5",
                String.format("M%f,%fv%fm0,%fl%f,%fm%f,%fl%f,%f",
                        x,
                        y,
                        mw,
                        -mw/2,
                        mas * orientation,
                        -mas,
                        -mas * orientation,
                        mas,
                        mas * orientation,
                        mas
                )
        );
        path.setAttribute("style","fill:none;stroke:black;stroke-width:0.2");
        return path;
    }


    Element createAnnotation (Document doc, String svgNS, double x1, double y1, double x2, double y2, String comment) {
        final double mw = 6, mas = 1.2, mininner = mas * 4;

        double xd = x2 - x1;
        double yd = y2 - y1;
        double ws = sqrt(xd*xd + yd*yd);

        double xds = xd / ws;
        double yds = yd / ws;

        boolean outerArrows = ws < mininner;

        Element group = doc.createElementNS(svgNS,"g");
        group.setAttribute("transform", String.format("matrix(%f,%f,%f,%f,%f,%f)", xds, yds, -yds, xds, x1, y1));
        group.appendChild(makeArrowDelim(doc,svgNS,0,0,mw,mas,outerArrows));
        group.appendChild(makeArrowDelim(doc,svgNS,ws,0,mw,mas,!outerArrows));
        Element line = doc.createElementNS(svgNS,"path");
        line.setAttribute("d", String.format(
                "M%f,%fh%f",
                outerArrows ? (- mas * 2) : 0,
                mw / 2.0,
                outerArrows ?  (ws + mas * 4) : ws
        ));
        line.setAttribute("style","fill:none;stroke:black;stroke-width:0.2");
        group.appendChild(line);
        TextElement text = new TextElement(doc,svgNS,comment,"ISOCPEUR",4);
        text.setX(ws/2 - text.getWidth()/2);
        text.setY(
                (text.getWidth() < ws - mas*2 - 2) ? (mw/2 + 1) :
                        (text.getWidth() < ws - 2) ? (mw/2 + mas + 1) :
                                (mw + 1)
        );
        group.appendChild(text.getElement());

        return group;
    }

    Element createLine (Document doc, String svgNS, double x1, double y1, double x2, double y2, String style) {
        SVGLineElement line = (SVGLineElement) doc.createElementNS(svgNS,"line");
        line.setAttribute("x1",String.valueOf(x1));
        line.setAttribute("y1",String.valueOf(y1));
        line.setAttribute("x2",String.valueOf(x2));
        line.setAttribute("y2",String.valueOf(y2));
        line.setAttribute("style",style);
        return line;
    }

    public BoxedElement renderSVG(Document doc, String svgNS) {
        String fillStroke = "fill: none; stroke-width: 0.5; stroke: #000;";
        String noFillStroke = "fill: none; stroke-width: 0.5; stroke: #000; stroke-dasharray: 1.0,0.5";

        double aScaled = a * 0.1;
        double bScaled = b * 0.1;

        SVGGElement group = (SVGGElement) doc.createElementNS(svgNS,"g");
        group.appendChild(createLine(doc,svgNS,0,0,aScaled,0, edges[0] ? fillStroke : noFillStroke ));
        group.appendChild(createLine(doc,svgNS,aScaled,0,aScaled,bScaled, edges[1] ? fillStroke : noFillStroke ));
        group.appendChild(createLine(doc,svgNS,0,bScaled,aScaled,bScaled, edges[2] ? fillStroke : noFillStroke ));
        group.appendChild(createLine(doc,svgNS,0,0,0,bScaled, edges[3] ? fillStroke : noFillStroke ));
        group.appendChild(createAnnotation(doc,svgNS,0,bScaled,aScaled,bScaled,String.format("%.1f mm",a)));
        group.appendChild(createAnnotation(doc,svgNS,aScaled,bScaled,aScaled,0,String.format("%.1f mm",b)));

        return new CustomBoxedElement(group, aScaled, bScaled);
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getWidth() {
        return width;
    }

    public boolean[] getEdges() {
        return edges;
    }
}
