package org.techdraw.sheets.doc;

/**
 * Stores page style information.
 * It's just data container - without encapsulation
 *
 * @author Miroslav Kravec
 */
public class PageStyle {
    public double marginTop = 15;
    public double marginRight = 10;
    public double marginBottom = 12;
    public double marginLeft = 10;

    /**
     * Shorthand to set margins. CSS-like order
     *
     * @param topBottom value specifying top and bottom margin
     * @param rightLeft value specifying right and left margin
     */
    public void setMargin(double topBottom, double rightLeft) {
        this.marginTop = this.marginBottom = topBottom;
        this.marginRight = this.marginLeft = rightLeft;
    }

    /**
     * Shorthand to set margins. CSS-like order
     *
     * @param marginTop
     * @param marginRight
     * @param marginBottom
     * @param marginLeft
     */
    public void setMargin(double marginTop, double marginRight, double marginBottom, double marginLeft) {
        this.marginTop = marginTop;
        this.marginRight = marginRight;
        this.marginBottom = marginBottom;
        this.marginLeft = marginLeft;
    }
}
