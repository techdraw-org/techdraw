package org.techdraw.sheets.containers;

import org.techdraw.sheets.api.BoxedElement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.max;

/**
 * @author Miroslav Kravec
 */
public class TableElement extends AbstractBoxedElement {

    private List<List<BoxedElement>> elements = new ArrayList<>();

    private double spacing = 0.0;


    public TableElement() {
        super("g");
    }

    public void addRow(BoxedElement... elements) {
        this.elements.add(new ArrayList<BoxedElement>(Arrays.asList(elements)));
        requestRecalculation();
    }

    @Override
    protected void recalculateSize() {
        setHeight(
                elements.stream()
                        .map(elements -> elements.stream().map(BoxedElement::getHeight).max(Double::compare).get())
                        .reduce(Double::sum)
                        .get()
        );
    }

    @Override
    protected void populateElementAttributes(Element e, Document doc, String svgNS) {
        e.setAttribute("transform", String.format("translate(%f,%f)", getX(),getY()));
        int columns = elements.stream().map(list -> list.size()).max(Integer::max).get();
        double[] columnWidths = new double[columns];
        for(int i = 0; i < columns; i++)
            columnWidths[i] = 0;

        elements.stream().forEachOrdered(boxedElements -> {
            for(int i = 0; i < boxedElements.size(); i++)
                columnWidths[i] = max(columnWidths[i], boxedElements.get(i).getWidth());
        });

        double x, y, ynext = 0;
        for(List<BoxedElement> boxedElements : elements) {
            y = ynext;
            x = 0;
            for(int i = 0; i < boxedElements.size(); i++) {
                BoxedElement element = boxedElements.get(i);
                // TODO: set available area, need a lot of refactoring
                element.setX(x - element.getX());
                element.setY(y - element.getY());
                e.appendChild(element.toSvgElement(doc,svgNS));
                ynext = max(ynext, y+element.getHeight());
                x += columnWidths[i] + spacing;
            }
        }
    }

    public double getSpacing() {
        return spacing;
    }

    public void setSpacing(double spacing) {
        this.spacing = spacing;
        requestRecalculation();
    }
}
