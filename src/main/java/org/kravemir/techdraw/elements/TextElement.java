package org.kravemir.techdraw.elements;

import org.apache.batik.ext.awt.g2d.DefaultGraphics2D;
import org.kravemir.techdraw.containers.AbstractBoxedElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.*;

/**
 * @author Miroslav Kravec
 */
public class TextElement extends AbstractBoxedElement {
    private String text;
    private final String font;
    private final double size;

    public TextElement(Document doc, String svgNS, String text, String font, double size) {
        super(doc, svgNS, "text");
        this.text = text;
        this.font = font;
        this.size = size;
    }

    @Override
    protected void recalculateSize() {
        Graphics graphics = new DefaultGraphics2D(false);
        FontMetrics metrics = graphics.getFontMetrics(new Font("ISOCPEUR",Font.PLAIN, (int) (size*100)));
        setHeight(metrics.getHeight()/100.0);
        setWidth(metrics.stringWidth(text)/100.0);
    }

    @Override
    protected void populateElementAttributes(Element e, Document doc, String svgNS) {
        e.setAttribute("x", String.valueOf(getX()));
        e.setAttribute("y", String.valueOf(getY()+getHeight()));
        e.setAttribute("style", String.format("font-family: '%s'; font-size: %f;", font, size));
        e.appendChild(doc.createTextNode(this.text));
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        if(text.contains("\n"))
            throw new RuntimeException("Single line text can't contain newline");
    }
}
