package org.techdraw.sheets;

import org.techdraw.sheets.api.BoxedElement;
import org.techdraw.sheets.api.PartGroup;
import org.techdraw.sheets.containers.GroupElement;
import org.techdraw.sheets.containers.TableElement;
import org.techdraw.sheets.elements.LineElement;
import org.techdraw.sheets.elements.TextElement;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Miroslav Kravec
 */
public class SimpleGroupDrawer implements DocPartDrawer {

    private PartGroup group;

    public SimpleGroupDrawer(PartGroup group) {
        this.group = group;
    }

    private DrawResult createContentGroup(PartGroup group, double xmax, double maxHeight) {
        List<BoxedElement> remainingElements = group.children.stream()
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

        DocPartDrawer nextDrawer = null;
        if(!remainingElements.isEmpty())
            nextDrawer = new SimpleGroupDrawer(new PartGroup(remainingElements, group.metadata));

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
        for(Map.Entry<String,String> e : group.metadata.entrySet()) {
            table.addRow(
                    new TextElement(e.getKey(),"ISOCPEUR", 5 ),
                    new TextElement(e.getValue(), "ISOCPEUR", 5)
            );
        }
        g.addChild(table);

        y = table.getHeight() + 8;
        g.addChild(new LineElement(0,0, xmax +10,0));
        g.addChild(new LineElement(0,y, xmax +10,y));

        DrawResult contentResult = createContentGroup(group, xmax, maxHeight - y);
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
