package osslabs.frgu;

import javax.xml.soap.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ������ on 09.06.2015.
 */
public class SoapBuilder {

    final static String rev_uri = "http://smev.gosuslugi.ru/rev111111";
    final static String rev1_uri = "http://rgu.lanit.ru/rev111111";
    final static String inc_uri = "http://www.w3.org/2004/08/xop/include";

    String senderCode;
    String senderName;
    String recipientCode;
    String recipientName;

    public SoapBuilder(String senderCode, String senderName, String recipientCode, String recipientName){
        this.senderCode = senderCode;
        this.senderName = senderName;
        this.recipientCode = recipientCode;
        this.recipientName = recipientName;
    }

    public SOAPMessage buildSoap(String action) throws SOAPException {

        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

        MessageFactory messageFactory = MessageFactory.newInstance();//(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();

        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("rev", rev_uri);
        envelope.addNamespaceDeclaration("rev1", rev1_uri);
        envelope.addNamespaceDeclaration("inc", inc_uri);

        SOAPBody soapBody = envelope.getBody();

        SOAPElement rev_Request = soapBody.addChildElement("Request", "rev");
        SOAPElement rev_Message = rev_Request.addChildElement("Message", "rev");

        // БЛОК <Sender>

        SOAPElement rev_Sender = rev_Message.addChildElement("Sender", "rev");
        SOAPElement rev_SenderCode = rev_Sender.addChildElement("Code", "rev");
        rev_SenderCode.addTextNode(senderCode);
        SOAPElement rev_SenderName = rev_Sender.addChildElement("Name", "rev");
        rev_SenderName.addTextNode(senderName);

        //БЛОК <Recipient>

        SOAPElement rev_Recipient = rev_Message.addChildElement("Recipient", "rev");
        SOAPElement rev_RecipientCode = rev_Recipient.addChildElement("Code", "rev");
        rev_RecipientCode.addTextNode(recipientCode);
        SOAPElement rev_RecipientName = rev_Recipient.addChildElement("Name", "rev");
        rev_RecipientName.addTextNode(recipientName);

        // --------------------------------

        SOAPElement rev_TypeCode = rev_Message.addChildElement("TypeCode","rev");
        rev_TypeCode.addTextNode("3");
        SOAPElement rev_Status = rev_Message.addChildElement("Status", "rev");
        rev_Status.addTextNode("REQUEST");
        SOAPElement rev_Date = rev_Message.addChildElement("Date", "rev");
        rev_Date.addTextNode(format.format(currentDate));


        SOAPElement rev_MessageData = rev_Request.addChildElement("MessageData", "rev");
        SOAPElement rev_AppData = rev_MessageData.addChildElement("AppData", "rev");

        if (action.equals("getCurrentSSN")){
            SOAPElement rev1_getChanges = rev_AppData.addChildElement("getChanges", "rev1");
            SOAPElement rev1_SSNInterval = rev1_getChanges.addChildElement("SSNInterval", "rev1");
            SOAPElement ssnFrom = rev1_SSNInterval.addChildElement("ssnFrom", "rev1");
            SOAPElement ssnTo = rev1_SSNInterval.addChildElement("ssnTo", "rev1");
        }

        soapMessage.saveChanges();

        return soapMessage;
    }
}
