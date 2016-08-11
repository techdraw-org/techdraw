package org.techdraw.desks;

import org.techdraw.sheets.api.BoxedElement;
import org.techdraw.sheets.containers.CustomBoxedElement;
import org.techdraw.sheets.containers.GroupElement;
import org.techdraw.sheets.elements.LineElement;
import org.techdraw.sheets.elements.TextElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static java.lang.Math.sqrt;

/**
 * @author Miroslav Kravec
 */
public class Desk {

    double a;
    double b;
    double width;
    boolean[] edges;

    public Desk() {
        this.edges = new boolean[]{false,false,false,false};
    }

    public Desk(double a, double b, double width, boolean[] edges) {
        this.a = a;
        this.b = b;
        this.width = width;
        this.edges = edges;
    }

    CustomBoxedElement makeArrowDelim(double x, double y, double mw, double mas, boolean outerArrows) {
        return new CustomBoxedElement(0,0) { // TODO: size
            @Override
            public Element toSvgElement(Document doc, String svgNS) {
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
        };
    }


    BoxedElement createAnnotation (double x1, double y1, double x2, double y2, String comment) {
            final double mw = 6, mas = 1.2, mininner = mas * 4;

            double xd = x2 - x1;
            double yd = y2 - y1;
            double ws = sqrt(xd*xd + yd*yd);

            double xds = xd / ws;
            double yds = yd / ws;

            boolean outerArrows = ws < mininner;

            GroupElement group = new GroupElement();
            group.setMatrixTransform(xds, yds, -yds, xds, x1, y1);
            group.addChild(makeArrowDelim(0,0,mw,mas,outerArrows));
            group.addChild(makeArrowDelim(ws,0,mw,mas,!outerArrows));
            group.addChild(new CustomBoxedElement(0,0) { // TODO: size
                @Override
                public Element toSvgElement(Document doc, String svgNS) {
                    Element line = doc.createElementNS(svgNS,"path");
                    line.setAttribute("d", String.format(
                            "M%f,%fh%f",
                            outerArrows ? (- mas * 2) : 0,
                            mw / 2.0,
                            outerArrows ?  (ws + mas * 4) : ws
                    ));
                    line.setAttribute("style","fill:none;stroke:black;stroke-width:0.2");
                    return line;
                }
            });

            TextElement text = new TextElement(comment,"ISOCPEUR",4);
            text.setX(ws/2 - text.getWidth()/2);
            text.setY(
                    (text.getWidth() < ws - mas*2 - 2) ? (mw/2 + 1) :
                            (text.getWidth() < ws - 2) ? (mw/2 + mas + 1) :
                                    (mw + 1)
            );
            group.addChild(text);

            return group;
    }

    BoxedElement createLine (double x1, double y1, double x2, double y2, String style) {
        return new LineElement(
                x1,
                y1,
                x2,
                y2,
                style
        );
    }

    public BoxedElement createElement() {
        String fillStroke = "fill: none; stroke-width: 0.5; stroke: #000;";
        String noFillStroke = "fill: none; stroke-width: 0.5; stroke: #000; stroke-dasharray: 1.0,0.5";

        double aScaled = a * 0.1;
        double bScaled = b * 0.1;

        GroupElement group = new GroupElement();
        group.addChild(createLine(0,0,aScaled,0, edges[0] ? fillStroke : noFillStroke ));
        group.addChild(createLine(aScaled,0,aScaled,bScaled, edges[1] ? fillStroke : noFillStroke ));
        group.addChild(createLine(0,bScaled,aScaled,bScaled, edges[2] ? fillStroke : noFillStroke ));
        group.addChild(createLine(0,0,0,bScaled, edges[3] ? fillStroke : noFillStroke ));
        group.addChild(createAnnotation(0,bScaled,aScaled,bScaled,String.format("%.1f mm",a)));
        group.addChild(createAnnotation(aScaled,bScaled,aScaled,0,String.format("%.1f mm",b)));
        group.setWidth(aScaled); // TODO: real sizes should be computed by GroupElement
        group.setHeight(bScaled);

        return group;
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
