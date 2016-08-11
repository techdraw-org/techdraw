package org.techdraw.sheets.api;

import java.util.Collection;
import java.util.Map;

/**
 * @author Miroslav Kravec
 */
public class PartGroup {
    public Collection<BoxedElement> children;
    public Map<String, String> metadata;

    public PartGroup() {
    }

    public PartGroup(Collection<BoxedElement> children, Map<String, String> metadata) {
        this.children = children;
        this.metadata = metadata;
    }
}
