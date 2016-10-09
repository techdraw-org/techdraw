package org.techdraw.sheets.doc.drawers;

import org.techdraw.sheets.doc.spi.DocDrawer;
import org.techdraw.sheets.elements.TextElement;

/**
 * Drawer which draws header/title
 *
 * @author Miroslav Kravec
 */
public class TitleDrawer implements DocDrawer {
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
