package org.techdraw.desks;

import org.techdraw.sheets.api.BoxedElement;
import org.techdraw.sheets.api.PartGroup;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DesksPartGroupGenerator {
    public DesksPartGroupGenerator() {
    }

    public Collection<PartGroup> createDeskGroups(Collection<Desk> desks) {
        Map<Double, List<Desk>> byWidth = desks.stream().collect(Collectors.groupingBy(Desk::getWidth));
        return byWidth.entrySet().stream().map(entry -> {
            Collection<BoxedElement> elements = entry.getValue().stream().map(Desk::createElement).collect(Collectors.toList());

            PartGroup partGroup = new PartGroup();
            partGroup.children = elements;
            partGroup.metadata = new HashMap<String, String>();
            partGroup.metadata.put("Desk decor:", "svetly buk");
            partGroup.metadata.put("Desk width:", String.format("%.1f", entry.getKey()));

            return partGroup;
        }).collect(Collectors.toList());
    }
}