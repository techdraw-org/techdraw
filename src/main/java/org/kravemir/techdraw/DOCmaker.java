package org.kravemir.techdraw;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.kravemir.techdraw.api.BoxedElement;
import org.kravemir.techdraw.containers.TableElement;
import org.kravemir.techdraw.deskdraw.Desk;
import org.kravemir.techdraw.elements.LineElement;
import org.kravemir.techdraw.elements.TextElement;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Miroslav Kravec
 */
public class DOCmaker {

    public static class ShowGroup {
        Element g;
        double h;

        public ShowGroup(Element g, double h) {
            this.g = g;
            this.h = h;
        }
    }

    public Collection<ShowGroup> createDeskGroups(Collection<Desk> desks, double xmax, Document doc, String svgNS) {
        xmax -= 10;
        Map<Double,List<Desk>> byWidth = desks.stream().collect(Collectors.groupingBy(part -> part.getWidth()));
        double finalXmax = xmax;
        return byWidth.entrySet().stream().map(entry -> {
            double x = 0;
            double y = 0, ynext = 0;
            Element g = doc.createElementNS(svgNS,"g");

            TableElement table = new TableElement(doc,svgNS);
            table.setSpacing(2);
            table.setX(5);
            table.setY(4);
            table.addRow(
                    new TextElement(doc,svgNS,"Desk decor:", "ISOCPEUR", 5 ),
                    new TextElement(doc,svgNS,"svetly buk", "ISOCPEUR", 5)
            );
            table.addRow(
                    new TextElement(doc,svgNS,"Desk width:", "ISOCPEUR", 5),
                    new TextElement(doc,svgNS,String.format("%.1f",entry.getKey()), "ISOCPEUR", 5)
            );
            g.appendChild(table.getElement());

            y = table.getHeight() + 8;

            g.appendChild(new LineElement(doc,svgNS,0,0,finalXmax+10,0).getElement());
            g.appendChild(new LineElement(doc,svgNS,0,y,finalXmax+10,y).getElement());

            y = ynext = y + 5;

            for(Desk part : entry.getValue()) {
                BoxedElement e = part.renderSVG(doc,svgNS);
                if(x + e.getWidth() > finalXmax) {
                    x = 0;
                    y = ynext + 15;
                }
                ynext = Math.max(ynext, y + e.getHeight());
                Element group = doc.createElementNS(svgNS,"g");
                group.setAttribute("transform",String.format("translate(%f,%f)",x+5,y));
                group.appendChild(e.getElement());
                g.appendChild(group);
                x += e.getWidth() + 15;
            }
            ynext += 15;
            g.appendChild(new LineElement(doc,svgNS,0,ynext,finalXmax+10,ynext).getElement());
            g.appendChild(new LineElement(doc,svgNS,0,0,0,ynext).getElement());
            g.appendChild(new LineElement(doc,svgNS,finalXmax+10,0,finalXmax+10,ynext).getElement());
            return new ShowGroup(g,ynext);
        }).collect(Collectors.toList());
    }


    public Document makeDoc(Collection<Desk> parts) {
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();
        Document doc = domImpl.createDocument(svgNS, "svg", null);

        Element svgRoot = doc.getDocumentElement();

        // Set the width and height attributes on the root 'svg' element.
        svgRoot.setAttributeNS(null, "width", "21cm");
        svgRoot.setAttributeNS(null, "height", "29.7cm");
        svgRoot.setAttributeNS(null, "viewBox", "0 0 210 297");


        double xoffset = 10, xmax = 210 - xoffset*2;
        double y = 15;

        Collection<ShowGroup> groups = createDeskGroups(
                parts.stream()
                        .filter(item -> item instanceof Desk)
                        .map(item -> (Desk)item).collect(Collectors.toList()),
                xmax,
                doc,
                svgNS
        );

        for(ShowGroup g : groups) {
            g.g.setAttribute("transform", String.format("translate(%f,%f)", xoffset,y));
            svgRoot.appendChild(g.g);
            y+=g.h + 10;
        }

        return doc;
    }
}
