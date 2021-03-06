package org.techdraw.desks;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.XMLResourceDescriptor;
import org.techdraw.sheets.*;
import org.techdraw.sheets.api.PartGroup;
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

/**
 * @author Miroslav Kravec
 */
public class DemoApp {
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
            new Desk(320, 210, 12, new boolean[] {true,true,true,true}),
            new Desk(320, 210, 12, new boolean[] {true,true,true,true})
    ));

    private final DesksPartGroupGenerator desksPartGroupGenerator = new DesksPartGroupGenerator();

    private SVGDocument[] prepareDocuments() {
        SimplePageDecorator pageDecorator = new SimplePageDecorator();
        pageDecorator.setHeaderText("TechDraw:desks, demo application");
        pageDecorator.setFooterText("Generated using TechDraw (http://techdraw.org/)");

        DocRenderer renderer = new DocRenderer();
        renderer.setPageDecorator(pageDecorator);

        Collection<PartGroup> groups = desksPartGroupGenerator.createDeskGroups(parts);
        List<DocPartDrawer> docPartDrawers = new ArrayList<>();
        docPartDrawers.add(new TitleDrawer("TechDraw:desks"));
        groups.stream().forEach(g -> docPartDrawers.add(new SimpleGroupDrawer(g)));

        Document[] documents = renderer.makeDoc(docPartDrawers);

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
        new DemoApp().run();
    }
}
