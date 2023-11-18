package org.quantum.synchronizer.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.quantum.synchronizer.entity.Staff;
import org.quantum.synchronizer.entity.StaffKey;
import org.quantum.synchronizer.entity.StaffTable;
import org.quantum.synchronizer.exception.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

/**
 * XmlParser it's a main class for parsing staff entities from xml and viceversa
 * It uses DOM parser for read xml and JAXB for write into xml
 */
public class XmlParser {

    private static final String STAFF_NAME_TAG = "staff";
    private static final String STAFF_DEP_CODE_TAG = "depCode";
    private static final String STAFF_DEP_JOB_TAG = "depJob";
    private static final String STAFF_DESCRIPTION_TAG = "description";
    private static final Logger log = LoggerFactory.getLogger(XmlParser.class);

    private XmlParser() {

    }

    public static XmlParser getInstance() {
        return XmlParserHolder.INSTANCE;
    }

    /**
     * Parse staff entities from provided xml file using DOM
     * 
     * @param xmlFile
     * @return Map<StaffKey, Staff>
     */
    public Map<StaffKey, Staff> parseFrom(File xmlFile) {
        log.info("parse {} to map", xmlFile.getName());
        var result = new HashMap<StaffKey, Staff>();
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            document.getDocumentElement().normalize();
            var staffList = document.getElementsByTagName(STAFF_NAME_TAG);
            for (int i = 0; i < staffList.getLength(); i++) {
                var staffNode = staffList.item(i);
                if (staffNode.getNodeType() == Node.ELEMENT_NODE) {
                    var staff = mapToStaff(staffNode);
                    var staffKey = new StaffKey(staff.getDepCode(), staff.getDepJob());
                    if (result.putIfAbsent(staffKey, staff) != null) {
                        var message = "Duplicate value for key %s".formatted(staffKey);
                        log.error(message);
                        throw new DuplicateKeyException(message);
                    }
                    result.put(new StaffKey(staff.getDepCode(), staff.getDepJob()), staff);
                }
            }
            log.info("parse {} to map complete", xmlFile);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            log.error("error during parse {}: {}", xmlFile, e);
            throw new RuntimeException(e);
        }
        log.info("result file parsing: {}", result);
        return result;
    }

    /**
     * Write every staff object from staffSet to xmlFile
     * 
     * @param xmlFile
     * @param staffSet
     */
    public void parseTo(File xmlFile, Set<Staff> staffSet) {
        log.info("write staff from db to {}", xmlFile);
        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(StaffTable.class);
            var marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(createStaffTable(staffSet), xmlFile);
            log.info("write to {} complete", xmlFile);
        } catch (JAXBException e) {
            log.error("error during write data to {}: {}", xmlFile, e);
            throw new RuntimeException(e);
        }
    }

    private Staff mapToStaff(Node node) {
        var staffElement = (Element) node;
        var depCode = staffElement.getElementsByTagName(STAFF_DEP_CODE_TAG).item(0).getTextContent();
        var depJob = staffElement.getElementsByTagName(STAFF_DEP_JOB_TAG).item(0).getTextContent();
        var description = staffElement.getElementsByTagName(STAFF_DESCRIPTION_TAG).item(0).getTextContent();
        return new Staff(depCode, depJob, description);
    }

    private StaffTable createStaffTable(Set<Staff> staffList) {
        var staffTable = new StaffTable();
        staffTable.setStaffList(staffList);
        return staffTable;
    }

    private static class XmlParserHolder {
        public static final XmlParser INSTANCE = new XmlParser();
    }

}
