package core;

import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: jemenake
 * Date: Jun 24, 2005
 * Time: 1:56:08 AM
 */
public class XMLFileUtils {

    private static final String patchLibXMLName = "patchlib";
    private static final String patchXMLName = "patch";
    private static final String authorXMLName = "author";
    private static final String dateXMLName = "date";
    private static final String commentXMLName = "comment";
    private static final String patchDataXMLName = "patchData";

    private static final boolean compressPatchData = true;

    public static void writePatchBasket(PatchBasket pb, String filename) {

        Element root = new Element(patchLibXMLName);
        Document doc = new Document(root);

        ArrayList pblist = pb.getPatchCollection();
        for(int i=0; i<pblist.size(); i++) {
            Patch patch = (Patch) pblist.get(i);
            Element patchElement = new Element(patchXMLName);
            patchElement.setAttribute(authorXMLName, patch.getAuthor());
            patchElement.setAttribute(dateXMLName, patch.getDate());
            patchElement.setAttribute(commentXMLName, patch.getComment());
            Element patchDataElement = new Element(patchDataXMLName);

            if(compressPatchData) {
                patchDataElement.setAttribute("encoding", "GZIP:Base64");
                patchDataElement.addContent(Base64.encodeBytes(patch.sysex, Base64.GZIP ));
            } else {
                patchDataElement.setAttribute("encoding", "Base64");
                patchDataElement.addContent(Base64.encodeBytes(patch.sysex));
            }
//            patchDataElement.addContent(org.apache.xerces.impl.dv.util.Base64.encode(patch.sysex));
            patchElement.addContent(patchDataElement);
            root.addContent(patchElement);
        }

        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        try {
          outputter.output(doc, new FileOutputStream(filename));
        }
        catch (IOException e) {
          ErrorMsg.reportStatus(e);
        }
    }

    public static void readPatchBasket(PatchBasket pb, String filename) {
        SAXBuilder builder = new SAXBuilder(false);

        // command line should offer URIs or file names
        try {
            Document doc = builder.build(filename);
            ErrorMsg.reportStatus(filename + " is valid.");

            Element root = doc.getRootElement();
            if(root.getName().equals(patchLibXMLName)) {
                List children = root.getChildren();
                for(int i=0; i<children.size(); i++) {
                    Element patchElement = (Element) children.get(i);
                    if(patchElement.getName().equals(patchXMLName)) {
                        ErrorMsg.reportStatus(authorXMLName + " : " + patchElement.getAttribute(authorXMLName));
                        ErrorMsg.reportStatus(dateXMLName + " : " + patchElement.getAttribute(dateXMLName));
                        ErrorMsg.reportStatus(commentXMLName + " : " + patchElement.getAttribute(commentXMLName));
                        Element patchDataElement = patchElement.getChild(patchDataXMLName);
//                        byte patchdata[] = org.apache.xerces.impl.dv.util.Base64.decode(patchDataElement.getText());
                        byte patchdata[] = Base64.decode(patchDataElement.getText());
                        ErrorMsg.reportStatus("Patch data length = " + patchdata.length);
                        Patch patch = new Patch(patchdata);
                        patch.setAuthor(patchElement.getAttribute(authorXMLName).getValue());
                        patch.setDate(patchElement.getAttribute(dateXMLName).getValue());
                        patch.setComment(patchElement.getAttribute(commentXMLName).getValue());
                        pb.pastePatch(patch);
                    } else {
                        ErrorMsg.reportStatus("Invalid patch element");
                    }
                }
            } else {
                ErrorMsg.reportStatus("Invalid patchlib element");
            }
        }
        // indicates a well-formedness or validity error
        catch (JDOMException e) {
            ErrorMsg.reportStatus(filename + " is not valid.");
            ErrorMsg.reportStatus(e.getMessage());
        }
        catch (IOException e) {
            ErrorMsg.reportStatus("Could not check " + filename);
            ErrorMsg.reportStatus(" because " + e.getMessage());
        }
    }

}
