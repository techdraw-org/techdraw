package org.techdraw.sheets;

import models.PageStyle;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.techdraw.sheets.api.BoxedElement;
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
    private static PageStyle DEFAULT_STYLE = new PageStyle();

    private PageDecorator pageDecorator = null;

    private PageStyle pageStyle;

    public Document[] makeDoc(Collection<DocPartDrawer> groups) {
        PageStyle pageStyle = this.pageStyle;
        if(pageStyle == null)
            pageStyle = DEFAULT_STYLE;

        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
        DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();

        List<Document> documents = new ArrayList<>();

        Document doc = null;

        double content_offsetX = pageStyle.marginLeft;
        double content_offsetY = pageStyle.marginTop;
        double content_sizeX = 210 - pageStyle.marginLeft - pageStyle.marginRight;
        double content_sizeY = 290 - pageStyle.marginBottom;

        if(pageDecorator != null) {
            content_offsetY += pageDecorator.headerHeight(pageStyle.marginTop);
            content_sizeY -= pageDecorator.headerHeight(pageStyle.marginTop);
            content_sizeY -= pageDecorator.footerHeight(pageStyle.marginBottom);
        }

        double y = content_offsetY;

        // TODO: find better fix
        Locale.setDefault(Locale.ENGLISH);
        for (DocPartDrawer g : groups) {
            DocPartDrawer current = g;
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

                    y = content_offsetY;
                } else {
                    svgRoot = doc.getDocumentElement();
                }

                DocPartDrawer.DrawResult dr = current.draw(content_sizeX, content_sizeY - y);
                BoxedElement be = dr.getBoxedElement();
                svgRoot.appendChild(GroupElement.translate(be, content_offsetX, y).toSvgElement(doc, svgNS));
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

    public PageStyle getPageStyle() {
        return pageStyle;
    }

    public void setPageStyle(PageStyle pageStyle) {
        this.pageStyle = pageStyle;
    }
}
