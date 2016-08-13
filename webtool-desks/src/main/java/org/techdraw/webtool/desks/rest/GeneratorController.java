package org.techdraw.webtool.desks.rest;

import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.techdraw.desks.Desk;
import org.techdraw.desks.DesksPartGroupGenerator;
import org.techdraw.sheets.DOCmaker;
import org.techdraw.sheets.api.PartGroup;
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
import java.util.Collection;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Miroslav Kravec
 */
@RestController
public class GeneratorController {

    private String renderSVG(ArrayList<Desk> desks) {
        Collection<PartGroup> groups = new DesksPartGroupGenerator().createDeskGroups(desks);
        Document[] documents = new DOCmaker().makeDoc(groups);

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
    public @ResponseBody ResponseEntity<String> generateSVG(@RequestBody ArrayList<Desk> request) {
        String svg = renderSVG(request);
        return ResponseEntity.ok()
                .contentLength(svg.length())
                .contentType(MediaType.parseMediaType("image/svg+xml"))
                .body(svg);
    }

    @RequestMapping(value = "/pdf", method = POST)
    public @ResponseBody ResponseEntity<byte[]> generatePDF(@RequestBody ArrayList<Desk> desks) {
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

            String cmdLine = String.format("rsvg-convert -f pdf -o %s %s", pdfFile.getAbsolutePath(), svgFile.getAbsolutePath());
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
