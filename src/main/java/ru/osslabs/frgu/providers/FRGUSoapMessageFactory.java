package ru.osslabs.frgu.providers;

import com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPMessageFactory1_1Impl;
import org.springframework.util.StringUtils;
import org.springframework.ws.InvalidXmlException;
import org.springframework.ws.soap.SoapMessageCreationException;
import org.springframework.ws.soap.saaj.SaajSoapEnvelopeException;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.TransportConstants;
import org.springframework.ws.transport.TransportInputStream;
import org.xml.sax.SAXParseException;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * Своя фабрика для обработки ответов от http://rgu.mosreg.ru/rgu/ws-services/registryInfoService
 */
public class FRGUSoapMessageFactory extends SaajSoapMessageFactory {

    private MessageFactory messageFactory;

    private boolean langAttributeOnSoap11FaultString = false;

    @Override
    public SaajSoapMessage createWebServiceMessage(InputStream inputStream) throws IOException {
        messageFactory = this.getMessageFactory();
        MimeHeaders mimeHeaders = parseMimeHeaders(inputStream);
        try {
            inputStream = checkForUtf8ByteOrderMark(inputStream);
            SOAPMessage saajMessage = messageFactory.createMessage(mimeHeaders, inputStream);
            postProcess(saajMessage);
            return new SaajSoapMessage(saajMessage, langAttributeOnSoap11FaultString, messageFactory);
        }
        catch (SOAPException ex) {
            // SAAJ 1.3 RI has a issue with handling multipart XOP content types which contain "startinfo" rather than
            // "start-info", so let's try and do something about it
            String contentType = StringUtils
                    .arrayToCommaDelimitedString(mimeHeaders.getHeader(TransportConstants.HEADER_CONTENT_TYPE));
            if (contentType.contains("startinfo") || contentType.contains("application/xop+xml")) {
                contentType = contentType.replace("startinfo", "start-info");
                if (messageFactory instanceof SOAPMessageFactory1_1Impl) {
                    contentType = contentType.replace("application/xop+xml", "text/xml");
                }
                mimeHeaders.setHeader(TransportConstants.HEADER_CONTENT_TYPE, contentType);
                try {
                    SOAPMessage saajMessage = messageFactory.createMessage(mimeHeaders, inputStream);
                    postProcess(saajMessage);
                    return new SaajSoapMessage(saajMessage,
                            langAttributeOnSoap11FaultString);
                }
                catch (SOAPException except) {
                    //Fall through
                }
            }
            throw new SoapMessageCreationException("Could not create message from InputStream: " + ex.getMessage(), ex);
        } catch (SaajSoapEnvelopeException ex) {
            SAXParseException parseException = getSAXParseException(ex);
            if (parseException != null) {
                throw new InvalidXmlException("Could not parse XML", parseException);
            } else {
                throw ex;
            }
        }
    }

    private SAXParseException getSAXParseException(Throwable ex) {
        if (ex instanceof SAXParseException) {
            return (SAXParseException) ex;
        } else if (ex.getCause() != null) {
            return getSAXParseException(ex.getCause());
        } else {
            return null;
        }
    }

    private MimeHeaders parseMimeHeaders(InputStream inputStream) throws IOException {
        MimeHeaders mimeHeaders = new MimeHeaders();
        if (inputStream instanceof TransportInputStream) {
            TransportInputStream transportInputStream = (TransportInputStream) inputStream;
            for (Iterator<String> headerNames = transportInputStream.getHeaderNames(); headerNames.hasNext();) {
                String headerName = headerNames.next();
                for (Iterator<String> headerValues = transportInputStream.getHeaders(headerName); headerValues.hasNext();) {
                    String headerValue = headerValues.next();
                    StringTokenizer tokenizer = new StringTokenizer(headerValue, ",");
                    while (tokenizer.hasMoreTokens()) {
                        mimeHeaders.addHeader(headerName, tokenizer.nextToken().trim());
                    }
                }
            }
        }
        return mimeHeaders;
    }

    /**
     * Checks for the UTF-8 Byte Order Mark, and removes it if present. The SAAJ RI cannot cope with these BOMs.
     *
     * @see <a href="http://jira.springframework.org/browse/SWS-393">SWS-393</a>
     * @see <a href="http://unicode.org/faq/utf_bom.html#22">UTF-8 BOMs</a>
     */
    private InputStream checkForUtf8ByteOrderMark(InputStream inputStream) throws IOException {
        PushbackInputStream pushbackInputStream = new PushbackInputStream(new BufferedInputStream(inputStream), 3);
        byte[] bytes = new byte[3];
        int bytesRead = pushbackInputStream.read(bytes);
        if (bytesRead != -1) {
            // check for the UTF-8 BOM, and remove it if there. See SWS-393
            if (!isByteOrderMark(bytes)) {
                pushbackInputStream.unread(bytes, 0, bytesRead);
            }
        }
        return pushbackInputStream;
    }

    private boolean isByteOrderMark(byte[] bytes) {
        return bytes.length == 3 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF;
    }
}
