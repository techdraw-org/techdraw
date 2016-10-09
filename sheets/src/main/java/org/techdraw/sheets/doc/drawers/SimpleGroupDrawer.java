package org.techdraw.sheets.doc.drawers;

import org.techdraw.sheets.doc.spi.DocDrawer;
import org.techdraw.sheets.elements.LineElement;
import org.techdraw.sheets.elements.TextElement;
import org.techdraw.sheets.elements.containers.GroupElement;
import org.techdraw.sheets.elements.containers.TableElement;
import org.techdraw.sheets.elements.spi.BoxedElement;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Miroslav Kravec
 */
public class SimpleGroupDrawer implements DocDrawer {

    public Collection<BoxedElement> elements;
    public Map<String, String> metadata;

    public SimpleGroupDrawer(Collection<BoxedElement> elements) {
        this(elements, new HashMap<>());
    }

    public SimpleGroupDrawer(Collection<BoxedElement> elements, Map<String, String> metadata) {
        this.elements = elements;
        this.metadata = metadata;
    }

    private DrawResult createContentGroup(double xmax, double maxHeight) {
        List<BoxedElement> remainingElements = elements.stream()
                .sorted((a,b) -> Double.compare(b.getHeight(),a.getHeight()))
                .collect(Collectors.toList());

        double innerOffset = 5;
        double x = 0;
        double y = innerOffset, ynext = 0;

        GroupElement contentGroup = new GroupElement();
        Iterator<BoxedElement> it = remainingElements.iterator();
        while(it.hasNext()) {
            BoxedElement e = it.next();

            if(x + e.getWidth() > xmax) {
                x = 0;
                y = ynext + 15;
            }
            if(y + e.getHeight() > maxHeight)
                break;

            ynext = Math.max(ynext, y + e.getHeight());
            contentGroup.addChild(GroupElement.translate(e, x + 5, y));
            x += e.getWidth() + 15;
            it.remove();
        }
        contentGroup.setHeight(ynext + innerOffset + 10);

        DocDrawer nextDrawer = null;
        if(!remainingElements.isEmpty())
            nextDrawer = new SimpleGroupDrawer(remainingElements, metadata);

        return new DrawResult(contentGroup, nextDrawer);
    }

    @Override
    public DrawResult draw(double maxWidth, double maxHeight) {
        double y = 0;
        double xmax = maxWidth - 10;

        GroupElement g = new GroupElement();

        TableElement table = new TableElement();
        table.setSpacing(2);
        table.setX(5);
        table.setY(4);
        for(Map.Entry<String,String> e : metadata.entrySet()) {
            table.addRow(
                    new TextElement(e.getKey(),"ISOCPEUR", 5 ),
                    new TextElement(e.getValue(), "ISOCPEUR", 5)
            );
        }
        g.addChild(table);

        y = table.getHeight() + 8;
        g.addChild(new LineElement(0,0, xmax +10,0));
        g.addChild(new LineElement(0,y, xmax +10,y));

        DrawResult contentResult = createContentGroup(xmax, maxHeight - y);
        GroupElement groupElement = (GroupElement) contentResult.getBoxedElement();
        groupElement.setMatrixTransform(1,0,0,1,0, y);

        y = y + groupElement.getHeight();

        g.addChild(groupElement);

        g.addChild(new LineElement(0,y, xmax +10,y));
        g.addChild(new LineElement(0,0,0,y));
        g.addChild(new LineElement(xmax +10,0, xmax +10,y));

        g.setHeight(y);

        return new DrawResult(g, contentResult.getNextDrawer());
    }
}
