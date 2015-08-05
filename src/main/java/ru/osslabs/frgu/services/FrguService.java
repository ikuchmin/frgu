package ru.osslabs.frgu.services;

import org.bson.types.ObjectId;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapVersion;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.osslabs.frgu.domain.FrguObject;
import ru.osslabs.frgu.domain.FrguObjectBuilder;
import ru.osslabs.frgu.domain.ObjectType;
import ru.osslabs.frgu.providers.FRGUSoapMessageFactory;
import ru.osslabs.frgu.providers.SoapBuilder;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ikuchmin on 22.06.15.
 */
public class FrguService extends WebServiceGatewaySupport {

    private SoapBuilder builder;

    public FrguService(SoapBuilder builder){
        this.builder = builder;

        FRGUSoapMessageFactory saajSoapMessageFactory = new FRGUSoapMessageFactory();
        saajSoapMessageFactory.setSoapVersion(SoapVersion.SOAP_11);
        saajSoapMessageFactory.afterPropertiesSet();
        setMessageFactory(saajSoapMessageFactory);

    }

    private class ChangeEl {
        public ChangeEl(Long id, Long ssn, String type, String status) {
            this.id = id;
            this.ssn = ssn;
            this.type = type;
            this.status = status;
        }

        Long id;
        Long ssn;
        String type;
        String status;
    }

    @Override
    protected void initGateway() throws Exception {
        super.initGateway();
    }

    public List<FrguObject> updateDataBase(long ssn) {

        HashMap<Long, ChangeEl> idList = new HashMap<>();

        HashMap<Long, FrguObject> dataList = new HashMap<>();

        try {
            SOAPElement message = this.builder.buildSoap("getChanges");

            Document o = sendRequestToFRGU(new DOMSource(message));

            if (o == null || getNodeByName(o, "faultstring") != null) {
                System.out.println("EvaluationError");
            }
            long currentSsn = Long.parseLong(getNodeByName(o, "currentSsn").getNodeValue(), 10);
            long tempSsn = ssn;

            if (currentSsn > tempSsn) {
                if (currentSsn - 1000 > tempSsn) {
                    while (currentSsn - 1000 > tempSsn) {
                        idList.putAll(createSSNObjectList(ssn, currentSsn));
                        tempSsn += 999;
                    }
                } else {
                    idList = createSSNObjectList(tempSsn, currentSsn);
                }

                for (Long index : idList.keySet()) {
                    if (dataList.get(index).getSsn() > idList.get(index).ssn) {
                        dataList.put(index,
                                getObjectData(
                                        index,
                                        idList.get(index).type
                                )
                        );
                    }
                }
            }

            message = this.builder.buildSoap("getRevokationList");

            o = sendRequestToFRGU(new DOMSource(message));

            if (o == null || getNodeByName(o, "faultstring") != null) {
                System.out.println("EvaluationError");
            }
            tempSsn = ssn;

            if (currentSsn > tempSsn) {
                if (currentSsn - 1000 > tempSsn) {
                    while (currentSsn - 1000 > tempSsn) {
                        idList.putAll(createSSNObjectList(ssn, currentSsn));
                        tempSsn += 999;
                    }
                } else {
                    idList = createSSNObjectList(tempSsn, currentSsn);
                }

                for (Long index : idList.keySet()) {
                    if (dataList.get(index).getSsn() > idList.get(index).ssn) {
                        dataList.put(index,
                                getObjectData(
                                        index,
                                        idList.get(index).type
                                )
                        );
                    }
                }
            }
        } catch (SOAPException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(dataList.values());
    }

    private Document sendRequestToFRGU(DOMSource src) {
        WebServiceTemplate wsTemplate = getWebServiceTemplate();

        return wsTemplate.sendSourceAndReceive("http://rgu.mosreg.ru/rgu/ws-services/registryInfoService",
                src, source -> {
                    Document doc = ((DOMSource) source).getNode().getOwnerDocument();
                    return doc;
                }
        );
    }

    private static String nodeToString(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
            System.out.println("nodeToString Transformer Exception");
        }
        return sw.toString();
    }

    private static void writeToFile(String filename, String message) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(filename, "UTF-8");
            writer.println(message);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Возвращает мапу изменений в промежутке между SSN
     * @param toSsn
     * @return
     * @throws SOAPException
     */
    private HashMap<Long, ChangeEl> createSSNObjectList(long fromSsn,
                                                    long toSsn) throws SOAPException {
        HashMap<Long, ChangeEl> idList = new HashMap<>();

        SOAPElement message = this.builder.buildSoap("getChanges", String.valueOf(fromSsn), String.valueOf(toSsn));
        Document o = sendRequestToFRGU(new DOMSource(message));
        if (o == null || getNodeByName(o, "faultstring") != null) {
            System.out.println("EvaluationError");
        }
        NodeList list = getNodesByName(o, "ObjectRef");
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            Long id = Long.parseLong(getNodeByName((Document) node, "objectId").getNodeValue(), 10);
            Long changeSsn = Long.parseLong(getNodeByName((Document) node, "objectSsn").getNodeValue(), 10);
            String type = getNodeByName((Document) node, "objectType").getNodeValue();
            String status = getNodeByName((Document) node, "currentStatus").getNodeValue();
            if (idList.get(id).ssn < changeSsn) {
                idList.put(id, new ChangeEl(id, changeSsn, type, status));
            }
        }

        message = this.builder.buildSoap("getRevokationList", String.valueOf(fromSsn), String.valueOf(toSsn));
        o = sendRequestToFRGU(new DOMSource(message));
        if (o == null || getNodeByName(o, "faultstring") != null) {
            System.out.println("EvaluationError");
        }
        list = getNodesByName(o, "ObjectRef");
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            Long id = Long.parseLong(getNodeByName((Document) node, "objectId").getNodeValue(), 10);
            Long changeSsn = Long.parseLong(getNodeByName((Document) node, "objectSsn").getNodeValue(), 10);
            String type = getNodeByName((Document) node, "objectType").getNodeValue();
            String status = getNodeByName((Document) node, "currentStatus").getNodeValue();
            if (idList.get(id).ssn < changeSsn) {
                idList.put(id, new ChangeEl(id, changeSsn, type, status));
            }
        }
        return null;
    }


    /**
     * Метод возвращает данные об объекте по id и типу.
     * @param uId
     * @param type
     * @return
     * @throws SOAPException
     */
    public FrguObject getObjectData(Long uId, String type) throws SOAPException {
        String action = type.equals("PsPassport") ? "getPsPassport" : "getRStateStructure";
        SOAPElement message = this.builder.buildSoap(action, String.valueOf(uId));
        Document o = sendRequestToFRGU(new DOMSource(message));
        if (o == null || getNodeByName(o, "faultstring") != null) {
            System.out.println("EvaluationError");
            return null;
        }
        else {
            Node dataNode = getNodeByName(o, type);
            String shortName = getNodeByName(dataNode, "shortTitle").getFirstChild().getNodeValue();
            String fullName = getNodeByName(dataNode, "fullTitle").getFirstChild().getNodeValue();
            long ssn = Long.parseLong(getNodeByName(dataNode, "ssn").getFirstChild().getNodeValue());
            String dataString = nodeToString(dataNode);
            //Example: 2012-09-05T11:06:56.460+04:00
            //
            Long changeDate = null;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            try {
                changeDate = format.parse(getNodeByName(o, "Date").getFirstChild().getNodeValue()).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long timestamp = System.currentTimeMillis();
            return new FrguObjectBuilder().
                    withId(ObjectId.get().toString()).
                    withFrguId(String.valueOf(uId)).
                    withFullName(fullName).
                    withShortName(shortName).
                    withSsn(ssn).
                    withData(dataString).
                    withObjectType(Enum.valueOf(ObjectType.class, type)).
                    withChangeDate(changeDate).
                    withTimestamp(timestamp).
                    build();
        }
    }

    private Node getNodeByName(Node doc, String name){
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "//*[local-name() = '" + name + "']";
        try {
            Node result = (Node) xPath.compile(expression).evaluate(doc, XPathConstants.NODE);
            if (xPath.compile(expression).evaluate(doc).isEmpty()) {
                return null;
            } else {
                return result.cloneNode(true);
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private NodeList getNodesByName(Document doc, String name){
        XPath xPath = XPathFactory.newInstance().newXPath();
        String expression = "//*[local-name() = '" + name + "']";
        try {
            NodeList result = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
            if (xPath.compile(expression).evaluate(doc).isEmpty()) {
                return null;
            } else {
                return result;
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
