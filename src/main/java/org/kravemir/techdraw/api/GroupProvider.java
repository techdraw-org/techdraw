package org.kravemir.techdraw.api;

import org.w3c.dom.Document;

/**
 * @author Miroslav Kravec
 */
public interface GroupProvider {
    SvgPartGroup generatePartGroup(Document doc, String svgNS);
}
