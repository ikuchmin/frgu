package osslabs.frgu;

import com.sun.xml.internal.messaging.saaj.soap.ver1_2.SOAPMessageFactory1_2Impl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.soap.SoapVersion;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.saaj.support.SaajUtils;

import javax.annotation.PostConstruct;

/**
 * Created by ikuchmin on 22.06.15.
 */
@Configuration
public class FRGUConfiguration {

    @Bean
    public FRGUClient frguClient() {
        FRGUClient client = new FRGUClient();
        return client;
    }

    @Bean
    public SaajSoapMessageFactory saajSoapMessageFactory() {
        SaajSoapMessageFactory saajSoapMessageFactory = new SaajSoapMessageFactory(new SOAPMessageFactory1_2Impl());
        saajSoapMessageFactory.setSoapVersion(SoapVersion.SOAP_12);
        return saajSoapMessageFactory;
    }
//    @PostConstruct
//    public void init() {
//        SaajUtils
//    }


}
