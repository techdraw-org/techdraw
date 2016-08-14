package org.techdraw.sheets;

import org.techdraw.sheets.api.BoxedElement;

/**
 * @author Miroslav Kravec
 */
public interface DocPartDrawer {

    DrawResult draw(double maxWidth, double maxHeight);

    class DrawResult {
        private BoxedElement boxedElement;
        private DocPartDrawer nextDrawer;

        public DrawResult(BoxedElement boxedElement, DocPartDrawer nextDrawer) {
            this.boxedElement = boxedElement;
            this.nextDrawer = nextDrawer;
        }

        public BoxedElement getBoxedElement() {
            return boxedElement;
        }

        public DocPartDrawer getNextDrawer() {
            return nextDrawer;
        }
    }
}
