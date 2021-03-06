package org.techdraw.desks;

import org.techdraw.sheets.api.BoxedElement;
import org.techdraw.sheets.containers.CustomBoxedElement;
import org.techdraw.sheets.containers.GroupElement;
import org.techdraw.sheets.elements.LineElement;
import org.techdraw.sheets.elements.TextElement;
import org.techdraw.sheets.elements.mix.NamedElementBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Random;

import static java.lang.Math.sqrt;

/**
 * @author Miroslav Kravec
 */
public class Desk {

    String key;
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

    private BoxedElement[] createFilling(double ls, double hs) {
        StringBuilder[] paths = new StringBuilder[8];
        for(int i = 0; i < 8; i++)
            paths[i] = new StringBuilder();

        Random rand = new Random();

        for(double ly = 1; ly < hs; ly += 0.5 + 1.0 * rand.nextDouble()) {
            double lx = 0;
            while (true) {
                lx += rand.nextDouble() * 5;
                if (lx + 0.1 > ls)
                    break;

                double w = 1 + rand.nextDouble() * rand.nextDouble() * 7;
                w = Math.min(ls - lx, w);
                int p = rand.nextInt(8);

                paths[p].append(String.format("M%f,%fh%f", lx, ly, w));
                lx += w + 1;
            }
        }
        BoxedElement[] boxedElements = new BoxedElement[8];
        for(int i = 0; i < 8; i++) {
            final String str = paths[i].toString();
            final int finalI = i;

            boxedElements[i] = new CustomBoxedElement(ls, hs) {
                @Override
                public Element toSvgElement(Document doc, String svgNS) {
                    char c = (char) ('c'-finalI);
                    if(c < 'a')
                        c = (char) ((int)c - 'a' + '0' + 10);
                    Element path = doc.createElementNS(svgNS, "path");
                    path.setAttribute("d",str);
                    path.setAttribute("style", String.format(
                            "fill: none; stroke-width: %f; stroke: #%c%c%c;", finalI * 0.05 / 8 + 0.05, c,c,c
                    ));
                    return path;
                }
            };
        }
        return boxedElements;
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
        group.addChild(createFilling(aScaled,bScaled));
        group.setWidth(aScaled); // TODO: real sizes should be computed by GroupElement
        group.setHeight(bScaled);

        BoxedElement result = group;

        if(key != null) {
            result = new NamedElementBuilder(key,group).build();
        }

        return result;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
