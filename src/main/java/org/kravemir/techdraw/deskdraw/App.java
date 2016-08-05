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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Miroslav Kravec
 */
public class App {
    static Collection<Desk> parts = Collections.unmodifiableList(Arrays.asList(
            new Desk(800, 100, 18, new boolean[] {true,true,true,true}),
            new Desk(100, 300, 18, new boolean[] {true,true,true,true}),
            new Desk(100, 300, 18, new boolean[] {true,true,true,true}),
            new Desk(100, 300, 18, new boolean[] {true,true,true,true}),
            new Desk(200, 200, 18, new boolean[] {true,true,true,true}),
            new Desk(600, 320, 18, new boolean[] {true,true,true,true}),
            new Desk(800, 200, 24, new boolean[] {true,true,true,true}),
            new Desk(100, 300, 18, new boolean[] {true,true,true,true}),
            new Desk(100, 300, 18, new boolean[] {true,true,true,true}),
            new Desk(800, 100, 18, new boolean[] {true,true,true,true}),
            new Desk(100, 300, 18, new boolean[] {true,true,true,true}),
            new Desk(100, 300, 18, new boolean[] {true,true,true,true}),
            new Desk(100, 300, 18, new boolean[] {true,true,true,true}),
            new Desk(200, 200, 18, new boolean[] {true,true,true,true}),
            new Desk(200, 200, 18, new boolean[] {true,true,true,true}),
            new Desk(600, 320, 18, new boolean[] {true,true,true,true}),
            new Desk(800, 200, 24, new boolean[] {true,true,true,true}),
            new Desk(800, 400, 24, new boolean[] {true,true,true,true}),
            new Desk(1000, 200, 12, new boolean[] {true,true,true,true}),
            new Desk(800, 400, 24, new boolean[] {true,true,true,true}),
            new Desk(1000, 200, 12, new boolean[] {true,true,true,true}),
            new Desk(320, 210, 12, new boolean[] {true,true,true,true})
    ));

    public Collection<PartGroup> createDeskGroups(Collection<Desk> desks) {
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

    private SVGDocument[] prepareDocuments() {
        Collection<PartGroup> groups = createDeskGroups(parts);
        Document[] documents = new DOCmaker().makeDoc(groups);

        return Arrays.stream(documents)
                .map(document -> {
                    try {
                        DOMSource domSource = new DOMSource(document);
                        StringWriter writer = new StringWriter();
                        StreamResult result = new StreamResult(writer);
                        TransformerFactory tf = TransformerFactory.newInstance();
                        Transformer transformer = tf.newTransformer();
                        transformer.transform(domSource, result);

                        String parser = XMLResourceDescriptor.getXMLParserClassName();
                        SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
                        return factory.createSVGDocument("", new ByteArrayInputStream(writer.toString().getBytes("UTF-8")));
                    } catch (TransformerException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(svgDocument -> svgDocument != null)
                .toArray(SVGDocument[]::new);
    }

    private void run() {
        JFrame f = new JFrame("TechDraw");
        f.setSize(1280,800);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setLayout(new BoxLayout(f.getContentPane(), BoxLayout.Y_AXIS));
        f.setVisible(true);

        SVGDocument[] documents = prepareDocuments();

        JSVGCanvas c = new JSVGCanvas();
        c.setSVGDocument(documents[0]);
        f.getContentPane().add(c);

        JComboBox<SVGDocument> comboBox = new JComboBox<>(documents);
        comboBox.setSelectedIndex(0);
        comboBox.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SVGDocument document = (SVGDocument) comboBox.getSelectedItem();
                c.setSVGDocument(document);
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.add(new Label("Show page"));
        bottomPanel.add(comboBox);
        bottomPanel.setMaximumSize(new Dimension(100000, 64));
        f.getContentPane().add(bottomPanel, BorderLayout.CENTER);

        f.setVisible(true);
    }

    public static void main(String[] args) {
        new App().run();
    }
}
