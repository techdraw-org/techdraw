package org.techdraw.sheets.doc.spi;

import org.techdraw.sheets.elements.spi.BoxedElement;

/**
 * Interface defines SPI for document drawers
 *
 * @author Miroslav Kravec
 */
public interface DocDrawer {

    /**
     * Draws contents of this drawer, and creates another drawer if contents won't fit into available space
     *
     * @param maxWidth - available width for drawing
     * @param maxHeight - available height for drawing
     * @return result element of drawing, and another drawer if it didn't fit the available space
     */
    DrawResult draw(double maxWidth, double maxHeight);

    class DrawResult {
        private BoxedElement boxedElement;
        private DocDrawer nextDrawer;

        public DrawResult(BoxedElement boxedElement, DocDrawer nextDrawer) {
            this.boxedElement = boxedElement;
            this.nextDrawer = nextDrawer;
        }

        public BoxedElement getBoxedElement() {
            return boxedElement;
        }

        public DocDrawer getNextDrawer() {
            return nextDrawer;
        }
    }
}
