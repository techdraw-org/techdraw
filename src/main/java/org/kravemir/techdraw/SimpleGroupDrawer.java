package org.kravemir.techdraw;

import java.util.HashSet;
import org.kravemir.techdraw.api.BoxedElement;
import org.kravemir.techdraw.api.PartGroup;
import org.kravemir.techdraw.containers.GroupElement;
import org.kravemir.techdraw.containers.TableElement;
import org.kravemir.techdraw.elements.LineElement;
import org.kravemir.techdraw.elements.TextElement;

import java.util.Map;
import java.util.Set;

/**
 * @author Miroslav Kravec
 */
public class SimpleGroupDrawer implements GroupDrawer {

    private PartGroup group;

    public SimpleGroupDrawer(PartGroup group) {
        this.group = group;
    }

    private DrawResult createContentGroup(PartGroup group, double xmax, double maxHeight) {
        Set<BoxedElement> remainingElements = new HashSet<>(group.children);

        double innerOffset = 5;
        double x = 0;
        double y = innerOffset, ynext = 0;

        GroupElement contentGroup = new GroupElement();
        for(BoxedElement e : group.children) {
            if(x + e.getWidth() > xmax) {
                x = 0;
                y = ynext + 15;
            }
            if(y + e.getHeight() > maxHeight)
                break;

            ynext = Math.max(ynext, y + e.getHeight());
            contentGroup.addChild(GroupElement.translate(e, x + 5, y));
            x += e.getWidth() + 15;
            remainingElements.remove(e);
        }
        contentGroup.setHeight(ynext + innerOffset + 10);

        GroupDrawer nextDrawer = null;
        if(remainingElements.size() > 0)
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
