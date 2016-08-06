package org.kravemir.techdraw;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.kravemir.techdraw.api.BoxedElement;
import org.kravemir.techdraw.api.PartGroup;
import org.kravemir.techdraw.containers.GroupElement;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

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
        double height = 290 - 15;
        double y = 15;

        // TODO: find better fix
        Locale.setDefault(Locale.ENGLISH);
        for (PartGroup g : groups) {
            GroupDrawer current = new SimpleGroupDrawer(g);
            do {
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

                GroupDrawer.DrawResult dr = current.draw(xmax, height - y);
                BoxedElement be = dr.getBoxedElement();
                svgRoot.appendChild(GroupElement.translate(be, xoffset, y).toSvgElement(doc, svgNS));
                y += be.getHeight() + 5;

                if (y >= 290 - 35.5 || dr.getNextDrawer() != null ) {
                    doc = null;
                }
                current = dr.getNextDrawer();
            } while ( current != null );
        }

        return documents.toArray(new Document[documents.size()]);
    }
}
