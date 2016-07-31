package org.kravemir.techdraw;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.kravemir.techdraw.api.BoxedElement;
import org.kravemir.techdraw.containers.TableElement;
import org.kravemir.techdraw.elements.LineElement;
import org.kravemir.techdraw.elements.TextElement;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Miroslav Kravec
 */
public class DOCmaker {

    public interface GroupGen {
        ShowGroup makeGroup(Document doc, String svgNS);
    }

    public static class ShowGroup {
        public Collection<BoxedElement> children;
        public Map<String,String> metadata;
        public double h;
        public double xmax;

        public Element toElement(Document doc, String svgNS) {
            double x = 0;
            double y = 0, ynext = 0;
            Element g = doc.createElementNS(svgNS,"g");

            TableElement table = new TableElement(doc,svgNS);
            table.setSpacing(2);
            table.setX(5);
            table.setY(4);
            for(Map.Entry<String,String> e : metadata.entrySet()) {
                table.addRow(
                        new TextElement(doc,svgNS,e.getKey(),"ISOCPEUR", 5 ),
                        new TextElement(doc,svgNS,e.getValue(), "ISOCPEUR", 5)
                );
            }
            g.appendChild(table.getElement());

            y = table.getHeight() + 8;

            g.appendChild(new LineElement(doc,svgNS,0,0, xmax +10,0).getElement());
            g.appendChild(new LineElement(doc,svgNS,0,y, xmax +10,y).getElement());

            y = ynext = y + 5;

            for(BoxedElement e : children) {
                if(x + e.getWidth() > xmax) {
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
            g.appendChild(new LineElement(doc,svgNS,0,ynext, xmax +10,ynext).getElement());
            g.appendChild(new LineElement(doc,svgNS,0,0,0,ynext).getElement());
            g.appendChild(new LineElement(doc,svgNS, xmax +10,0, xmax +10,ynext).getElement());
            this.h = ynext;
            return g;
        }
    }


    public Document makeDoc(Collection<GroupGen> groupGens) {
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

        // TODO: padding
        Collection<ShowGroup> groups = groupGens.stream().map(
                groupGen -> {
                    ShowGroup g = groupGen.makeGroup(doc,svgNS);
                    g.xmax = xmax - 10;
                    return g;
                }
        ).collect(Collectors.toList());

        for(ShowGroup g : groups) {
            Element e = g.toElement(doc,svgNS);
            e.setAttribute("transform", String.format("translate(%f,%f)", xoffset,y));
            svgRoot.appendChild(e);
            y+=g.h + 10;
        }

        return doc;
    }
}
