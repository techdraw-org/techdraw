package org.techdraw.sheets;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.techdraw.sheets.api.BoxedElement;
import org.techdraw.sheets.api.PartGroup;
import org.techdraw.sheets.containers.GroupElement;
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
public class DocRenderer {
    private PageDecorator pageDecorator = null;

    public Document[] makeDoc(Collection<PartGroup> groups) {
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();

        List<Document> documents = new ArrayList<>();

        Document doc = null;

        double xoffset = 10, xmax = 210 - xoffset * 2;
        double ystart = 5 + (pageDecorator != null ? pageDecorator.headerHeight() : 0), y;
        double height = 290 - 5 - (pageDecorator != null ? pageDecorator.footerHeight() : 0);

        y = ystart;

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

                    y = ystart;
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

        if(pageDecorator != null)
            documents.stream().forEach(d -> pageDecorator.applyPageDecoration(d.getDocumentElement(), d, svgNS));

        return documents.toArray(new Document[documents.size()]);
    }

    public PageDecorator getPageDecorator() {
        return pageDecorator;
    }

    public void setPageDecorator(PageDecorator pageDecorator) {
        this.pageDecorator = pageDecorator;
    }
}
