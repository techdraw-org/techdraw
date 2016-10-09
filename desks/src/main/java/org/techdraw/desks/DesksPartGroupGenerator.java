package org.techdraw.desks;

import org.techdraw.sheets.doc.drawers.SimpleGroupDrawer;
import org.techdraw.sheets.doc.spi.DocDrawer;
import org.techdraw.sheets.elements.spi.BoxedElement;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DesksPartGroupGenerator {
    public DesksPartGroupGenerator() {
    }

    public Collection<DocDrawer> createDeskGroups(Collection<Desk> desks) {
        Map<Double, List<Desk>> byWidth = desks.stream().collect(Collectors.groupingBy(Desk::getWidth));
        return byWidth.entrySet().stream().map(entry -> {
            Collection<BoxedElement> elements = entry.getValue().stream().map(Desk::createElement).collect(Collectors.toList());

            HashMap<String, String> metadata = new HashMap<>();
            metadata.put("Desk decor:", "svetly buk");
            metadata.put("Desk width:", String.format("%.1f", entry.getKey()));

            return new SimpleGroupDrawer(elements, metadata);
        }).collect(Collectors.toList());
    }
}