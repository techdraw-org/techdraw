package org.techdraw.sheets.elements.mix;

import org.techdraw.sheets.api.BoxedElement;
import org.techdraw.sheets.containers.GroupElement;
import org.techdraw.sheets.elements.TextElement;

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
