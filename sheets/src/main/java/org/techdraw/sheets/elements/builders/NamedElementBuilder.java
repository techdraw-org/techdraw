package org.techdraw.sheets.elements.builders;

import org.techdraw.sheets.elements.TextElement;
import org.techdraw.sheets.elements.containers.GroupElement;
import org.techdraw.sheets.elements.spi.BoxedElement;

/**
 * @author Miroslav Kravec
 */
public class NamedElementBuilder {
    private String nameStr;
    private BoxedElement element;

    public NamedElementBuilder(String nameStr, BoxedElement element) {
        this.nameStr = nameStr;
        this.element = element;
    }

    public BoxedElement build() {
        final int spacing = 3;

        TextElement text = new TextElement(nameStr, "ISOCPEUR", 5);
        GroupElement content = GroupElement.translate(element, 0, text.getHeight() + spacing);

        GroupElement g = new GroupElement();
        g.addChild(text);
        g.addChild(content);
        g.setWidth(Math.max(text.getWidth(),element.getWidth()));
        g.setHeight(text.getHeight() + element.getHeight() + spacing);
        return g;
    }
}
