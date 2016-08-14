package org.techdraw.sheets;

import org.techdraw.sheets.elements.TextElement;

/**
 * @author Miroslav Kravec
 */
public class TitleDrawer implements DocPartDrawer {
    private final String titleText;

    public TitleDrawer(String titleText) {
        this.titleText = titleText;
    }

    @Override
    public DrawResult draw(double maxWidth, double maxHeight) {
        TextElement title = new TextElement(titleText, "ISOCPEUR", 15);
        return new DrawResult(title,null);
    }
}
