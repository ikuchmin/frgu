package ru.osslabs.frgu;

//import org.apache.catalina.security.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import osslabs.frgu.FRGUClient;
import osslabs.frgu.FRGUConfiguration;
import osslabs.frgu.SoapBuilder;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;

//@ComponentScan
//@SpringBootApplication
//@SpringApplicationConfiguration(classes = {FRGUConfiguration.class})
//@EnableAutoConfiguration
public class Application {

    public static void main(String[] args) {

        ApplicationContext ctx = SpringApplication.run(FRGUConfiguration.class, args);
        FRGUClient frguClient = ctx.getBean(FRGUClient.class);
        SoapBuilder soapBuilder = new SoapBuilder("test_code","test_name","test_code","test_name");

        SOAPMessage soap = null;
        try {
            soap = soapBuilder.buildSoap("getCurrentSSN");
            frguClient.getFRGUMethod(soap);
        } catch (SOAPException | IOException e) {
            e.printStackTrace();
        }
    }
}