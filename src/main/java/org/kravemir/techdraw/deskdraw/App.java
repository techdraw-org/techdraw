package org.kravemir.techdraw.deskdraw;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.XMLResourceDescriptor;
import org.kravemir.techdraw.DOCmaker;
import org.kravemir.techdraw.api.BoxedElement;
import org.kravemir.techdraw.api.PartGroup;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGDocument;

import javax.swing.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Miroslav Kravec
 */
public class App {


    public static Collection<PartGroup> createDeskGroups(Collection<Desk> desks) {
        Map<Double,List<Desk>> byWidth = desks.stream().collect(Collectors.groupingBy(Desk::getWidth));
        return byWidth.entrySet().stream().map(entry ->  {
            Collection<BoxedElement> elements = entry.getValue().stream().map(Desk::createElement).collect(Collectors.toList());

            PartGroup partGroup = new PartGroup();
            partGroup.children = elements;
            partGroup.metadata = new HashMap<>();
            partGroup.metadata.put("Desk decor:", "svetly buk");
            partGroup.metadata.put("Desk width:", String.format("%.1f",entry.getKey()));

            return partGroup;
        }).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        Collection<Desk> parts = Arrays.asList(
                new Desk(1000, 200, 18, new boolean[] {true,true,true,true}),
                new Desk(1000, 200, 18, new boolean[] {true,true,true,true}),
                new Desk(1000, 200, 12, new boolean[] {true,true,true,true})
        );

        Collection<PartGroup> groups = createDeskGroups(parts);

        Document doc = new DOCmaker().makeDoc(groups);

        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);

            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
            SVGDocument document = factory.createSVGDocument("", new ByteArrayInputStream(writer.toString().getBytes("UTF-8")));

            JSVGCanvas c = new JSVGCanvas();
            c.setSVGDocument(document);

            JFrame f = new JFrame("TechDraw");
            f.setSize(1280,800);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.getContentPane().add(c);
            f.setVisible(true);
        } catch (TransformerException ex) {
            throw new RuntimeException(ex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}