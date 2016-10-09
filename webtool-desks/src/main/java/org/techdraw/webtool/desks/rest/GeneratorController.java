package org.techdraw.webtool.desks.rest;

import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.techdraw.desks.Desk;
import org.techdraw.sheets.*;
import org.techdraw.sheets.api.BoxedElement;
import org.techdraw.webtool.desks.models.DeskMaterialModel;
import org.techdraw.webtool.desks.models.DesksModel;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Miroslav Kravec
 */
@RestController
public class GeneratorController {

    private static String getNullIfEmpty(String str) {
        if(str != null && "".equals(str.trim()))
            return null;
        return str;
    }

    private Document[] renderDocuments(DesksModel model) {
        // clear empty values
        model.documentTitle = getNullIfEmpty(model.documentTitle);
        model.pageHeader = getNullIfEmpty(model.pageHeader);
        model.pageFooter = getNullIfEmpty(model.pageFooter);


        SimplePageDecorator pageDecorator = new SimplePageDecorator();
        pageDecorator.setHeaderText(model.pageHeader);
        if(model.pageFooter != null)
            pageDecorator.setFooterText(model.pageFooter + ", Generated using TechDraw (http://techdraw.org/)");
        else
            pageDecorator.setFooterText("Generated using TechDraw (http://techdraw.org/)");

        DocRenderer renderer = new DocRenderer();
        renderer.setPageDecorator(pageDecorator);
        renderer.setPageStyle(model.pageStyle);

        List<DocPartDrawer> docPartDrawers = new ArrayList<>();

        // add title, if present
        if(model.documentTitle != null)
            docPartDrawers.add(new TitleDrawer(model.documentTitle));

        // add groups
        for(int i = 0; i < model.materials.size(); i++ ) {
            int finalI = i;
            DeskMaterialModel material = model.materials.get(i);

            try {
                List<BoxedElement> elements = model.desks.stream()
                        .filter(deskModel -> Integer.valueOf(deskModel.material) == finalI)
                        .map(m -> new Desk(m.a, m.b, m.width, m.edges).createElement())
                        .collect(Collectors.toList());

                HashMap<String, String> metadata = new HashMap<>();
                metadata.put("Desk decor:", material.decor);
                metadata.put("Desk width:", String.format("%.1f", material.width));
                docPartDrawers.add(new SimpleGroupDrawer(elements, metadata));
            } catch (Exception e)  {
                System.err.println("ignoring: " + material);
                e.printStackTrace();
            }
        };

        return renderer.makeDoc(docPartDrawers);
    }

    private String renderSVG(DesksModel model) {
        Document[] documents = renderDocuments(model);

        try {
            DOMSource domSource = new DOMSource(documents[0]);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping(value = "/svg", method = POST /* TODO:, produces = "image/svg+xml"*/)
    public @ResponseBody ResponseEntity<String> generateSVG(@RequestBody DesksModel request) {
        String svg = renderSVG(request);
        return ResponseEntity.ok()
                .contentLength(svg.length())
                .contentType(MediaType.parseMediaType("image/svg+xml"))
                .body(svg);
    }

    @RequestMapping(value = "/pdf", method = POST)
    public @ResponseBody ResponseEntity<byte[]> generatePDF(@RequestBody DesksModel desks) {
        String svg = renderSVG(desks);
        byte[] pdf = transformToPDF(svg);

        return ResponseEntity.ok()
                .contentLength(pdf.length)
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private byte[] transformToPDF(String content) {
        File svgFile = null, pdfFile = null;
        try {
            svgFile = File.createTempFile("svg",".svg");
            pdfFile = File.createTempFile("pdf",".pdf");

            FileOutputStream svgOut = new FileOutputStream(svgFile);
            svgOut.write(content.getBytes(StandardCharsets.UTF_8));
            svgOut.flush();
            svgOut.close();

            //String cmdLine = String.format("rsvg-convert -f pdf -o %s %s", pdfFile.getAbsolutePath(), svgFile.getAbsolutePath());
            String cmdLine = String.format("inkscape %s --export-pdf=%s", svgFile.getAbsolutePath(), pdfFile.getAbsolutePath());
            System.out.println(cmdLine);
            Process process = Runtime.getRuntime().exec(cmdLine);
            process.waitFor();
            IOUtils.readLines(process.getInputStream());
            IOUtils.readLines(process.getErrorStream());

            return IOUtils.toByteArray(new FileInputStream(pdfFile));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(svgFile != null && svgFile.exists())
                svgFile.delete();
            if(pdfFile != null && pdfFile.exists())
                pdfFile.delete();
        }
    }
}
