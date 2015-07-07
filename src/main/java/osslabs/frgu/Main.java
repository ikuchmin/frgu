package osslabs.frgu;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;

public class Main {

    static final String SERVICE_URL = "http://rgu.mosreg.ru/rgu/ws-services/registryInfoService";

    public static void main(String[] args) throws Exception {

        SoapBuilder soapBuilder = new SoapBuilder("test_code","test_name","test_code","test_name");

        SOAPMessage soap = soapBuilder.buildSoap("getCurrentSSN");
        //soap.getMimeHeaders().removeAllHeaders();
        //soap.getMimeHeaders().addHeader("Content-Type", "text/xml;charset=UTF-8");
        //soap.getMimeHeaders().removeHeader("Content-Type");
        //soap.getMimeHeaders().addHeader("Content-Type", "Multipart/Related");
        //soap.getMimeHeaders().addHeader("Content-Type", "text/xml");

        SoapUtil.printSOAP(soap);

        SOAPMessage response = SoapUtil.sendSoapMessage(soap, SERVICE_URL);
        SoapUtil.printSOAP(response);


    }


}
