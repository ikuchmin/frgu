package osslabs.frgu;

import com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.SOAPMessageFactory1_2Impl;
import org.springframework.ws.client.core.SourceExtractor;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by ikuchmin on 22.06.15.
 */
public class FRGUClient extends WebServiceGatewaySupport {

    @Override
    protected void initGateway() throws Exception {
        super.initGateway();
    }

    public String getFRGUMethod(SOAPMessage message) throws IOException, SOAPException {
        System.out.println("Request: " + message);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        message.writeTo(byteArrayOutputStream);
        StreamSource streamSource = new StreamSource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        SaajSoapMessageFactory saajSoapMessageFactory = new SaajSoapMessageFactory(null);
        saajSoapMessageFactory.setSoapVersion(SoapVersion.SOAP_12);
        saajSoapMessageFactory.afterPropertiesSet();
//        saajSoapMessageFactory.setSoapVersion(SoapVersion.SOAP_12);
        setMessageFactory(saajSoapMessageFactory);

        Object o = getWebServiceTemplate().sendSourceAndReceive("http://rgu.mosreg.ru/rgu/ws-services/registryInfoService",
                streamSource, source -> {
                    System.out.println("Response: " + source);
                    return null;
                });

        return null;
    }
}
