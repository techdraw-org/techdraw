package org.techdraw.sheets;

import org.techdraw.sheets.api.BoxedElement;

/**
 * @author Miroslav Kravec
 */
public interface GroupDrawer {

    DrawResult draw(double maxWidth, double maxHeight);

    class DrawResult {
        private BoxedElement boxedElement;
        private GroupDrawer nextDrawer;

        public DrawResult(BoxedElement boxedElement, GroupDrawer nextDrawer) {
            this.boxedElement = boxedElement;
            this.nextDrawer = nextDrawer;
        }

        public BoxedElement getBoxedElement() {
            return boxedElement;
        }

        public GroupDrawer getNextDrawer() {
            return nextDrawer;
        }
    }
}
