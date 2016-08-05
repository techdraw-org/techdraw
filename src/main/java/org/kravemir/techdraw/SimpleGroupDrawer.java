package org.kravemir.techdraw;

import org.kravemir.techdraw.api.BoxedElement;
import org.kravemir.techdraw.api.PartGroup;
import org.kravemir.techdraw.containers.GroupElement;
import org.kravemir.techdraw.containers.TableElement;
import org.kravemir.techdraw.elements.LineElement;
import org.kravemir.techdraw.elements.TextElement;

import java.util.Map;

/**
 * @author Miroslav Kravec
 */
public class SimpleGroupDrawer implements GroupDrawer {

    private PartGroup group;

    public SimpleGroupDrawer(PartGroup group) {
        this.group = group;
    }

    private GroupElement createContentGroup(PartGroup group, double xmax) {
        double innerOffset = 5;
        double x = 0;
        double y = innerOffset, ynext = 0;

        GroupElement contentGroup = new GroupElement();
        for(BoxedElement e : group.children) {
            if(x + e.getWidth() > xmax) {
                x = 0;
                y = ynext + 15;
            }
            ynext = Math.max(ynext, y + e.getHeight());
            GroupElement g2 = new GroupElement();
            g2.setMatrixTransform(1,0,0,1,x+5,y);
            g2.addChild(e);
            contentGroup.addChild(g2);
            x += e.getWidth() + 15;
        }
        contentGroup.setHeight(ynext + innerOffset + 10);

        return contentGroup;
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

        GroupElement groupElement = createContentGroup(group, xmax);
        groupElement.setMatrixTransform(1,0,0,1,0, y);

        y = y + groupElement.getHeight();

        g.addChild(groupElement);

        g.addChild(new LineElement(0,y, xmax +10,y));
        g.addChild(new LineElement(0,0,0,y));
        g.addChild(new LineElement(xmax +10,0, xmax +10,y));

        g.setHeight(y);

        return new DrawResult(g, null);
    }
}
