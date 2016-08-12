package org.techdraw.webtool.desks.rest;

import org.springframework.http.HttpHeaders;
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
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Miroslav Kravec
 */
@RestController
public class GeneratorController {

    @RequestMapping(value = "/svg", method = POST /* TODO:, produces = "image/svg+xml"*/)
    public @ResponseBody ResponseEntity<String> generateSVG(@RequestBody ArrayList<Desk> request) {
        Collection<PartGroup> groups = new DesksPartGroupGenerator().createDeskGroups(request);
        Document[] documents = new DOCmaker().makeDoc(groups);

        try {
            DOMSource domSource = new DOMSource(documents[0]);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            String image = writer.toString();

            return ResponseEntity.ok()
                    .contentLength(image.length())
                    .contentType(MediaType.parseMediaType("image/svg+xml"))
                    .body(image);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}
