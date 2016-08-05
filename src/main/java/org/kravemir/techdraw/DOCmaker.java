package org.kravemir.techdraw;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.kravemir.techdraw.api.BoxedElement;
import org.kravemir.techdraw.api.PartGroup;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Miroslav Kravec
 */
public class DOCmaker {

    public Document[] makeDoc(Collection<PartGroup> groups) {
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();

        List<Document> documents = new ArrayList<>();

        Document doc = null;

        double xoffset = 10, xmax = 210 - xoffset * 2;
        double y = 15;

        for (PartGroup g : groups) {
            Element svgRoot;
            if (doc == null) {
                doc = domImpl.createDocument(svgNS, "svg", null);
                documents.add(doc);

                svgRoot = doc.getDocumentElement();

                // Set the width and height attributes on the root 'svg' element.
                svgRoot.setAttributeNS(null, "width", "21cm");
                svgRoot.setAttributeNS(null, "height", "29.7cm");
                svgRoot.setAttributeNS(null, "viewBox", "0 0 210 297");

                y = 15;
            } else {
                svgRoot = doc.getDocumentElement();
            }

            GroupDrawer.DrawResult dr = new SimpleGroupDrawer(g).draw(xmax, 9999.0);
            BoxedElement be = dr.getBoxedElement();
            Element e = be.toSvgElement(doc, svgNS);
            e.setAttribute("transform", String.format("translate(%f,%f)", xoffset, y));
            svgRoot.appendChild(e);
            y += be.getHeight() + 5;

            if (y > 290) {
                doc = null;
            }
        }

        return documents.toArray(new Document[documents.size()]);
    }
}
